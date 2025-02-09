package com.goldinvoice0.billingsoftware

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goldinvoice0.billingsoftware.Adapter.CustomerListAdapter
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.ViewModel.CustomerViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentCustomerListBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class customerList : Fragment(), CustomerInputDialog.OnCustomerInputListener {


    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding!!
    private val customerViewModel: CustomerViewModel by viewModels()
    private lateinit var adapter: CustomerListAdapter  // Class-level adapter
    private var isFromOrderList: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)

        binding.customerRecyclerView.visibility = View.INVISIBLE
        binding.placeHolderView.visibility = View.INVISIBLE

        val bundle = arguments
        isFromOrderList = bundle!!.getBoolean("FromOrderList")

        binding.addCustomer.setOnClickListener {
            val dialog = CustomerInputDialog()
            dialog.setOnCustomerInputListener(this)
            dialog.show(parentFragmentManager, "CustomerInputDialog")
        }


        setUpSearchView()

        setupRecyclerView()

        // Check if we have arguments (coming from contact selection)
        if (arguments?.getString("CustomerName") != null) {

            handleContactData()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_contect_button -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.contactLoading.visibility = View.VISIBLE
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.action_customerList_to_contactListFragment)
                        }
                    }
                    true
                }

                else -> false
            }
        }


        // Toggle placeholder visibility based on data

        return binding.root
    }


    // In the customerList class, add this after setupRecyclerView():
    private fun setupSwipeToDelete() {

        // Add swipe to delete functionality
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // Drag directions (0 means no drag)
            ItemTouchHelper.LEFT // Swipe directions (only right swipe)
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // We don't want drag & drop
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val deletedCustomer = adapter.currentList[position]

                // Show confirmation dialog
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Delete the customer
                        customerViewModel.delete(deletedCustomer)

                        // Show undo snackbar
                        Snackbar.make(
                            binding.root,
                            "Customer deleted",
                            Snackbar.LENGTH_LONG
                        ).setAction("UNDO") {
                            // Restore the customer if undo is clicked
                            customerViewModel.insert(deletedCustomer)
                        }.show()

                    }
                    .setNegativeButton("No") { _, _ ->
                        // Cancel deletion and restore the item view
                        adapter!!.notifyItemChanged(position)
                    }
                    .show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,  // dX will now be negative when swiping left
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val deleteIcon = ContextCompat.getDrawable(
                    requireContext(),
                    android.R.drawable.ic_menu_delete
                )
                val background = ColorDrawable(Color.RED)

                val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + deleteIcon.intrinsicHeight

                // Adjust background bounds for left swipe
                background.setBounds(
                    itemView.right + dX.toInt(),  // Start from right edge
                    itemView.top,
                    itemView.right,  // End at right edge
                    itemView.bottom
                )
                background.draw(c)

                // Draw delete icon for left swipe
                if (dX < 0) {  // Swiping to the left
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        // Attach the touch helper to the RecyclerView
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.customerRecyclerView)
    }

    private fun setupRecyclerView() {
        adapter = CustomerListAdapter { customer ->
            // Handle item click
            val bundle = Bundle()
            bundle.putString("name", customer.name)
            bundle.putString("email", customer.number)
            bundle.putString("address", customer.address)

            //go tho the next fragment with the customer details
            if (isFromOrderList) {
                findNavController().navigate(R.id.action_customerList_to_orderInput, bundle)
            } else if (!isFromOrderList) {
                findNavController().navigate(R.id.action_customerList_to_newBill, bundle)
            }

            //Future Idea ->
            // When Add customer is clicked (app create a new customer with the help of dialog and show in the recyclerview)
            // When Customer is clicked (app will select the customer and next button is get bold and get enabled)
        }

        binding.customerRecyclerView.adapter = adapter

        binding.customerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@customerList.adapter
        }

        // Add this line
        setupSwipeToDelete()

        customerViewModel.allCustomers.observe(viewLifecycleOwner) { customers ->
            customers?.let {
                adapter.setOriginalList(it)
            }
            updateVisibility(customers.isNotEmpty())
        }
    }

    private fun setUpSearchView() {
        val searchBar = binding.topAppBar.menu.findItem(R.id.action_search).actionView as SearchView
        searchBar.queryHint = "Search Customers..."  // Updated hint text
        searchBar.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })

        searchBar.setOnCloseListener {
            searchBar.onActionViewCollapsed()
            adapter.filter("")  // Reset to show all customers
            true
        }
    }

    private fun performSearch(query: String) {
        adapter.filter(query)
    }

    private fun updateVisibility(hasData: Boolean) {
        binding.customerRecyclerView.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.placeHolderView.visibility = if (hasData) View.GONE else View.VISIBLE
    }


    override fun onCustomerInput(name: String, contact: String, address: String) {
        Log.d("CustomerList", "Customer Input Received: $name, $contact, $address")
        Toast.makeText(
            requireContext(),
            "Name: $name, Contact: $contact, Address: $address",
            Toast.LENGTH_LONG
        ).show()
        // Handle the input (e.g., save to database or pass to another fragment)
        addCustomer(name, contact, address)
    }


    fun addCustomer(name: String, email: String, address: String) {
        if (name.isBlank()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isBlank()) {
            Toast.makeText(requireContext(), "Contact cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (address.isBlank()) {
            Toast.makeText(requireContext(), "Address cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        customerViewModel.insert(Customer(0, name, address, email))
        binding.customerRecyclerView.visibility = View.VISIBLE
        binding.placeHolderView.visibility = View.GONE
    }

    private fun handleContactData() {
        // Safely handle arguments, providing null as default
        arguments?.let { bundle ->
            val customerName = bundle.getString("CustomerName")
            val customerNumber = bundle.getString("CustomerNumber")


            // Show dialog with pre-filled data
            showCustomerInputDialog(customerName, customerNumber)


            // Clear the arguments after using them to prevent reuse on configuration changes
            arguments?.clear()
        }
    }

    private fun showCustomerInputDialog(customerName: String?, customerNumber: String?) {
        val dialog = CustomerInputDialog.newInstance(customerName, customerNumber)
        dialog.setOnCustomerInputListener(object : CustomerInputDialog.OnCustomerInputListener {
            override fun onCustomerInput(name: String, contact: String, address: String) {
                Log.d("CustomerList", "Customer Input Received: $name, $contact, $address")
                Toast.makeText(
                    requireContext(),
                    "Name: $name, Contact: $contact, Address: $address",
                    Toast.LENGTH_LONG
                ).show()
                // Handle the input (e.g., save to database or pass to another fragment)
                addCustomer(name, contact.replace(" ", ""), address)

            }
        })
        dialog.show(childFragmentManager, "CustomerInputDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}