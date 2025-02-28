package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.JewelryItemAdapter
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.JewelryType
import com.goldinvoice0.billingsoftware.databinding.FragmentOrderInputContainer2Binding

class OrderInputContainer2 : Fragment(), JewelryInputBottomSheet.JewelryInputListener {
    private var _binding: FragmentOrderInputContainer2Binding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val itemsAdapter = JewelryItemAdapter(
        onEditClick = { item, position -> handleEdit(item, position) },
        onDeleteClick = { item, position -> handleDelete(item, position) },
        onStatusChange = { item, position -> handleStatusChange(item, position) }
    )
    private var items = mutableListOf<JewelryItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderInputContainer2Binding.inflate(inflater, container, false)
        setupRecyclerView()
        setupAddButton()
        observeItems()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.itemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }
    }

    private fun setupAddButton() {
        binding.addItemButton.setOnClickListener {
            showBottomSheet()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Done -> {
                    findNavController().popBackStack()
                    true
                }

                else -> false
            }
        }


    }

    private fun showBottomSheet(position: Int? = null, item: JewelryItem? = null) {
        val bottomSheet = JewelryInputBottomSheet().apply {
            setListener(this@OrderInputContainer2)
            if (position != null && item != null) {
                setEditItem(position, item)
            }
        }
        bottomSheet.show(childFragmentManager, "JewelryInputBottomSheet")
    }

    private fun observeItems() {
        if (arguments?.getBoolean("EditJewelleryItem") == true) {
            sharedViewModel.jewelleryItems.removeObservers(viewLifecycleOwner)
            sharedViewModel.jewelleryItems.observe(viewLifecycleOwner) { paymentsList ->
                items.clear()
                items.addAll(paymentsList)
                itemsAdapter.submitList(paymentsList.toList())
            }
        }
    }

    override fun onJewelryItemAdded(item: JewelryItem) {
        items.add(item)
        itemsAdapter.submitList(items.toList())
        sharedViewModel.addJewelleryItem(item)
    }

    override fun onJewelryItemUpdated(position: Int, item: JewelryItem) {
        items[position] = item
        itemsAdapter.submitList(items.toList())
        sharedViewModel.updateJewelryItem(position, item)
    }

    private fun handleEdit(item: JewelryItem, position: Int) {
        showBottomSheet(position, item)
    }

    private fun handleDelete(item: JewelryItem, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { _, _ ->
                items.removeAt(position)
                itemsAdapter.submitList(items.toList())
                sharedViewModel.removeJewelryItem(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleStatusChange(item: JewelryItem, position: Int) {
        items[position] = item
        itemsAdapter.submitList(items.toList())
        sharedViewModel.updateJewelryItem(position,item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}