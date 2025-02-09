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


class OrderInputContainer2 : Fragment() {


    private var _binding: FragmentOrderInputContainer2Binding? = null
    private val binding get() = _binding!!

    private val itemsAdapter = JewelryItemAdapter(
        onEditClick = { item, position -> handleEdit(item, position) },
        onDeleteClick = { item, position -> handleDelete(item, position) },
        onStatusChange = { item, position -> handleStatusChange(item, position) }
    )
    private var items = mutableListOf<JewelryItem>()

    private val sharedViewModel : SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderInputContainer2Binding.inflate(inflater, container, false)


        setupItemTypeDropdown()
        setupRecyclerView()
        setupAddItemButton()




        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Done -> {

                    findNavController().popBackStack()
                    true
                }

                else -> false
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        if (arguments?.getBoolean("EditJewelleryItem") == true) {
            sharedViewModel.jewelleryItems.removeObservers(viewLifecycleOwner) // Prevent multiple observers
            sharedViewModel.jewelleryItems.observe(viewLifecycleOwner) { paymentsList ->
                items.clear()
                items.addAll(paymentsList) // Avoid referencing the same list
                itemsAdapter.submitList(paymentsList.toList()) // Ensure a new list is created
            }
        }
        super.onStart()
    }

    private fun setupItemTypeDropdown() {
        val itemTypes = JewelryType.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, itemTypes)
        binding.itemTypeDropdown.setAdapter(adapter)

        binding.itemTypeDropdown.setOnItemClickListener { _, _, position, _ ->
            toggleSizeInput(JewelryType.values()[position])
        }
    }

    private fun toggleSizeInput(type: JewelryType) {
        binding.sizeInputLayout.visibility = when (type) {
            JewelryType.RING -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun setupRecyclerView() {
        binding.itemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }
    }

    private fun setupAddItemButton() {
        binding.addItemButton.setOnClickListener {
            if (validateInputs()) {
                addItem()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (binding.jewelryNameInput.text.isNullOrEmpty() ||
            binding.weightInput.text.isNullOrEmpty() ||
            binding.rateInput.text.isNullOrEmpty()
        ) {
            showError("Please fill all required fields")
            return false
        }

        val type = binding.itemTypeDropdown.text.toString()
        if (type.isEmpty()) {
            showError("Please select item type")
            return false
        }

        if (type == JewelryType.RING.name && binding.sizeInput.text.isNullOrEmpty()) {
            showError("Please enter ring size")
            return false
        }

        return true
    }

    private fun addItem() {
        val type = JewelryType.valueOf(binding.itemTypeDropdown.text.toString())
        val item = JewelryItem(
            type = type,
            name = binding.jewelryNameInput.text.toString(),
            weight = binding.weightInput.text.toString().toDouble(),
            goldRate = binding.rateInput.text.toString().toDouble(),
            size = if (type == JewelryType.RING) binding.sizeInput.text.toString().toDouble() else null,
            notes = binding.notesInput.text.toString().takeIf { it.isNotEmpty() }
        )

        items.add(item)
        itemsAdapter.submitList(items.toList())
        sharedViewModel.addJewelleryItem(item)
        clearInputs()
    }

    private fun clearInputs() {
        binding.apply {
            itemTypeDropdown.text?.clear()
            jewelryNameInput.text?.clear()
            weightInput.text?.clear()
            rateInput.text?.clear()
            sizeInput.text?.clear()
            notesInput.text?.clear()
            sizeInputLayout.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun handleEdit(item: JewelryItem, position: Int) {
        // Fill the input fields with item data
        binding.apply {
            itemTypeDropdown.setText(item.type.name)
            jewelryNameInput.setText(item.name)
            weightInput.setText(item.weight.toString())
            rateInput.setText(item.goldRate.toString())
            if (item.type == JewelryType.RING) {
                sizeInputLayout.visibility = View.VISIBLE
                sizeInput.setText(item.size?.toString())
            }
            notesInput.setText(item.notes)

            // Change add button to update
            addItemButton.text = "Update Item"
            addItemButton.setOnClickListener {
                if (validateInputs()) {
                    updateItem(position)
                }
            }
        }
    }

    private fun updateItem(position: Int) {
        val type = JewelryType.valueOf(binding.itemTypeDropdown.text.toString())
        val updatedItem = JewelryItem(
            type = type,
            name = binding.jewelryNameInput.text.toString(),
            weight = binding.weightInput.text.toString().toDouble(),
            goldRate = binding.rateInput.text.toString().toDouble(),
            size = if (type == JewelryType.RING) binding.sizeInput.text.toString().toDouble() else null,
            notes = binding.notesInput.text.toString().takeIf { it.isNotEmpty() },
            status = items[position].status  // Preserve the status
        )

        items[position] = updatedItem
        itemsAdapter.submitList(items.toList())
        sharedViewModel.updateJewelryItem(position, updatedItem)
        clearInputs()
        resetAddButton()
    }

    private fun handleDelete(item: JewelryItem, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { _, _ ->

                val updatedPayments = items.toMutableList() // Create a new list
                updatedPayments.removeAt(position)
                items = updatedPayments // Update reference
                itemsAdapter.submitList(items.toList()) // Ensure adapter gets a fresh list
                sharedViewModel.removeJewelryItem(position)

            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleStatusChange(item: JewelryItem, position: Int) {
        items[position] = item
        itemsAdapter.submitList(items.toList())
    }

    private fun resetAddButton() {
        binding.addItemButton.text = "Add Item"
        binding.addItemButton.setOnClickListener {
            if (validateInputs()) {
                addItem()
            }
        }
    }
}