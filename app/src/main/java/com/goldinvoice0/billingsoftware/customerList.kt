package com.goldinvoice0.billingsoftware

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.CustomerListAdapter
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.ViewModel.CustomerViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentCustomerListBinding
import com.google.android.material.snackbar.Snackbar
import com.yourpackage.ui.bottomsheet.CustomerBottomSheetFragment


import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class customerList : Fragment(), CustomerBottomSheetFragment.CustomerDataListener {
    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomerViewModel by activityViewModels()
    private lateinit var customerAdapter: CustomerListAdapter

    // Source fragment identifier and navigation
    private var sourceFragmentId: String? = null
    private var customNavigationAction: Int? = null
    private var selectionMode: Boolean = false
    private var customerBottomSheet: CustomerBottomSheetFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)

        // Check for navigation parameters only if they exist in arguments
        arguments?.let { bundle ->

            // Extract navigation parameters
            sourceFragmentId = bundle.getString(ARG_SOURCE_FRAGMENT)
            customNavigationAction = bundle.getInt(ARG_NAVIGATION_ACTION, -1)
            if (customNavigationAction == -1) customNavigationAction = null
            selectionMode = bundle.getBoolean(ARG_SELECTION_MODE, false)

        }

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        setupRecyclerView()
        setupSearchView()
        setupFab()
        setupToolbar()
        observeCustomers()

    }

    private fun setupRecyclerView() {
        customerAdapter = CustomerListAdapter(
            onItemClick = { customer ->
                handleCustomerSelection(customer)
            },
            onDeleteClick = { customer, position ->
                handleCustomerDeletion(customer, position)
            },
            onEditClick = { customer ->
                handleCustomerEdit(customer)
            }
        )


        binding.customerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = customerAdapter
            setHasFixedSize(true)
        }


        binding.customerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Scrolling down - hide binding.addCustomer with animation
                if (dy > 10 && binding.addCustomer.isShown) {
                    binding.addCustomer.animate()
                        .translationY(binding.addCustomer.height * 2f)
                        .setInterpolator(AccelerateInterpolator(2f))
                        .setDuration(300)
                        .withEndAction { binding.addCustomer.visibility = View.GONE }
                        .start()
                }
                // Scrolling up - show binding.addCustomer with animation
                else if (dy < -10 && !binding.addCustomer.isShown) {
                    binding.addCustomer.visibility = View.VISIBLE
                    binding.addCustomer.animate()
                        .translationY(0f)
                        .setInterpolator(DecelerateInterpolator(2f))
                        .setDuration(300)
                        .start()
                }
            }
        })


    }

    private fun handleCustomerSelection(customer: Customer) {
        val result = Bundle().apply {
            putParcelable("selectedCustomer", customer)
        }

        when {
            selectionMode -> {
                Log.d("customerList", "Selection mode")
                setFragmentResult("customerRequest", result)
                findNavController().navigateUp()
            }

        }
    }

    private fun openCustomerBottomSheet(customerToEdit: Customer? = null) {
        customerBottomSheet = if (customerToEdit != null) {
            CustomerBottomSheetFragment.newInstance(customerToEdit)
        } else {
            CustomerBottomSheetFragment.newInstance()
        }
        customerBottomSheet?.setCustomerDataListener(this)
        customerBottomSheet?.show(childFragmentManager, "CustomerSheet")
    }

    private fun setupSearchView() {
        with(binding.topAppBar.menu.findItem(R.id.action_search).actionView as SearchView) {
            queryHint = "Search Customers..."
            inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { customerAdapter.filter(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { customerAdapter.filter(it) }
                    return true
                }
            })
            setOnCloseListener {
                onActionViewCollapsed()
                customerAdapter.filter("")
                true
            }
        }
    }


    private fun showDeleteConfirmation(customer: Customer, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Customer")
            .setMessage("Are you sure you want to delete ${customer.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCustomer(customer)
            }
            .setNegativeButton("Cancel") { _, _ ->
                customerAdapter.notifyItemChanged(position)
            }
            .show()
    }

    private fun deleteCustomer(customer: Customer) {
        viewModel.delete(customer)
        Snackbar.make(binding.root, "Customer deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") { viewModel.insert(customer) }
            .show()
    }

    private fun setupFab() {
        binding.addCustomer.setOnClickListener {
            showCustomerBottomSheet()
        }
    }

    private fun showCustomerBottomSheet() {
        val bottomSheet = CustomerBottomSheetFragment.newInstance()
        bottomSheet.setCustomerDataListener(this)
        bottomSheet.show(childFragmentManager, "CustomerBottomSheet")
    }

    // Implement the listener to receive customer data
    override fun onCustomerSaved(
        firstName: String,
        lastName: String,
        phone: String,
        customerId: String,
        streetAddress: String,
        city: String,
        state: String,
        imageUri: Uri
    ) {
        val customerBeingEdited = customerBottomSheet?.customerToEdit

        if (customerBeingEdited != null) {
            // We're updating an existing customer
            val updatedCustomer = Customer(
                id = customerBeingEdited.id, // Keep the existing ID
                name = "$firstName $lastName",
                address = streetAddress,
                address2 = "$city, $state",
                number = phone,
                customerId = customerId,
                imageUrl = imageUri.toString()
            )
            viewModel.update(updatedCustomer)
            Toast.makeText(requireContext(), "Customer updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            // We're creating a new customer
            val newCustomer = Customer(
                0, // SQLite will assign a new ID
                name = "$firstName $lastName",
                address = streetAddress,
                address2 = "$city, $state",
                number = phone,
                customerId = customerId,
                imageUrl = imageUri.toString()
            )
            viewModel.insert(newCustomer)
            Toast.makeText(requireContext(), "Customer added successfully", Toast.LENGTH_SHORT).show()
        }
        // Don't need to update the UI - viewModel will trigger LiveData updates automatically
    }

    override fun handleCustomerDeletion(customer: Customer, position: Int) {
        showDeleteConfirmation(customer, position)
    }

    // And implement handleCustomerEdit:
    override fun handleCustomerEdit(customer: Customer) {
        openCustomerBottomSheet(customer)
    }


    private fun setupToolbar() {
        binding.topAppBar.apply {
//            title = if (selectionMode) "Select Customer" else "Customers"

            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_contect_button -> {
                        true
                    }

                    else -> false
                }
            }
        }
    }


    private fun observeCustomers() {
        viewModel.allCustomers.observe(viewLifecycleOwner) { customers ->
            customers?.let {
                customerAdapter.setOriginalList(it)
                updateVisibility(it.isNotEmpty())
            }
        }
    }

    private fun updateVisibility(hasData: Boolean) {
        binding.customerRecyclerView.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.placeHolderView.visibility = if (hasData) View.GONE else View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SOURCE_FRAGMENT = "source_fragment"
        private const val ARG_NAVIGATION_ACTION = "navigation_action"
        private const val ARG_SELECTION_MODE = "selection_mode"

        fun newInstance(
            sourceFragmentId: String? = null,
            customNavigationAction: Int? = null,
            selectionMode: Boolean = false
        ): customerList {
            return customerList().apply {
                arguments = Bundle().apply {
                    sourceFragmentId?.let { putString(ARG_SOURCE_FRAGMENT, it) }
                    customNavigationAction?.let { putInt(ARG_NAVIGATION_ACTION, it) }
                    putBoolean(ARG_SELECTION_MODE, selectionMode)
                }
            }
        }
    }
}
