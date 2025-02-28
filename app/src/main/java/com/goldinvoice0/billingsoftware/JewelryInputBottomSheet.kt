package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.JewelryType
import com.goldinvoice0.billingsoftware.databinding.FragmentJewelleryInputBottomSheetBinding
import com.goldinvoice0.billingsoftware.databinding.FragmentPaymentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class JewelryInputBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentJewelleryInputBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    interface JewelryInputListener {
        fun onJewelryItemAdded(item: JewelryItem)
        fun onJewelryItemUpdated(position: Int, item: JewelryItem)
    }

    private var listener: JewelryInputListener? = null
    private var editPosition: Int? = null
    private var editItem: JewelryItem? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJewelleryInputBottomSheetBinding.inflate(inflater, container, false)


        // Make bottom sheet full screen
        dialog?.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true

                // Set the height to match parent
                sheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
        setupSaveButton()

        // Populate fields if editing
        editItem?.let { populateFields(it) }
    }

    private fun setupDropdowns() {
        // Setup Item Type Dropdown
        val itemTypes = JewelryType.values().map { it.name }
        val itemTypeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, itemTypes).apply {
            setDropDownViewResource(R.layout.dropdown_item)
        }

        binding.itemTypeDropdown.apply {
            setAdapter(itemTypeAdapter)
            setDropDownBackgroundResource(R.color.grayLight1)
        }


        // Setup Jewelry Name Dropdown
        val jewelryNames = resources.getStringArray(R.array.listOfJewelleryItems)
        val jewelryNameAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, jewelryNames).apply {
            setDropDownViewResource(R.layout.dropdown_item)
        }

        binding.jewelryNameInput.apply {
            setAdapter(jewelryNameAdapter)
            setDropDownBackgroundResource(R.color.grayLight1)
        }

        // Set default selection for item type
        if (binding.itemTypeDropdown.text.isEmpty()) {
            binding.itemTypeDropdown.setText(itemTypes[1], false)
            toggleSizeInput(JewelryType.values()[1])
        }

        binding.itemTypeDropdown.setOnItemClickListener { _, _, position, _ ->
            toggleSizeInput(JewelryType.values()[position])
        }
    }

    private fun toggleSizeInput(type: JewelryType) {
        binding.sizeInputLayout?.visibility = when (type) {
            JewelryType.RING -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            try {
                if (validateInputs()) {
                    val item = createJewelryItem()
                    if (editPosition != null) {
                        listener?.onJewelryItemUpdated(editPosition!!, item)
                    } else {
                        listener?.onJewelryItemAdded(item)
                    }
                    dismiss()
                }
            } catch (e: Exception) {
                showError("Error saving item: ${e.message}")
            }
        }
    }

    private fun validateInputs(): Boolean {
        with(binding) {
            when {
                itemTypeDropdown.text.isNullOrEmpty() -> {
                    showError("Please select item type")
                    return false
                }
                jewelryNameInput.text.isNullOrEmpty() -> {
                    showError("Please enter jewelry name")
                    return false
                }
                weightInput.text.isNullOrEmpty() -> {
                    showError("Please enter weight")
                    return false
                }
                rateInput.text.isNullOrEmpty() -> {
                    showError("Please enter rate")
                    return false
                }
                itemTypeDropdown.text.toString() == JewelryType.RING.name &&
                        sizeInput?.text.isNullOrEmpty() -> {
                    showError("Please enter ring size")
                    return false
                }
            }
            return true
        }
    }

    private fun createJewelryItem(): JewelryItem {
        try {
            val typeText = binding.itemTypeDropdown.text?.toString()
                ?: throw IllegalStateException("Item type cannot be null")

            val type = JewelryType.valueOf(typeText)
            val name = binding.jewelryNameInput.text?.toString()
                ?: throw IllegalStateException("Jewelry name cannot be null")

            val weightText = binding.weightInput.text?.toString()
                ?: throw IllegalStateException("Weight cannot be null")
            val weight = weightText.toDoubleOrNull()
                ?: throw IllegalStateException("Invalid weight format")

            val rateText = binding.rateInput.text?.toString()
                ?: throw IllegalStateException("Rate cannot be null")
            val rate = rateText.toDoubleOrNull()
                ?: throw IllegalStateException("Invalid rate format")

            val size = if (type == JewelryType.RING) {
                binding.sizeInput?.text?.toString()?.toDoubleOrNull()
            } else null

            val notes = binding.notesInput?.text?.toString()?.takeIf { it.isNotEmpty() }

            return JewelryItem(
                type = type,
                name = name,
                weight = weight,
                goldRate = rate,
                size = size,
                notes = notes,
            )
        } catch (e: Exception) {
            throw IllegalStateException("Error creating jewelry item: ${e.message}")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun populateFields(item: JewelryItem) {
        binding.apply {
            itemTypeDropdown.setText(item.type.name, false)
            jewelryNameInput.setText(item.name)
            weightInput.setText(item.weight.toString())
            rateInput.setText(item.goldRate.toString())
            if (item.type == JewelryType.RING) {
                sizeInputLayout?.visibility = View.VISIBLE
                sizeInput?.setText(item.size?.toString())
            }
            notesInput?.setText(item.notes)
            saveButton.text = "Update Item"
        }
    }

    fun setListener(listener: JewelryInputListener) {
        this.listener = listener
    }

    fun setEditItem(position: Int, item: JewelryItem) {
        editPosition = position
        editItem = item
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}