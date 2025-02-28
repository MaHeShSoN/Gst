package com.goldinvoice0.billingsoftware

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.goldinvoice0.billingsoftware.databinding.BottomSheetItemInputBinding as BottomSheetItemInputBinding1

class ItemInputBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetItemInputBinding1? = null
    private val binding get() = _binding!!
    private val viewModel: JewelryViewModel by activityViewModels()

    var onItemAdded: ((ItemModel) -> Unit)? = null
    var itemToEdit: ItemModel? = null
    private var editPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetItemInputBinding1.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                setupFullHeight(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = false

            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFullScreenUI()
        setupSpinners()
        setupButtons()

        // Change button text if editing
        if (itemToEdit != null) {
            binding.saveButton.text = "Update"
            binding.saveButton.icon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.check_24px)
        } else {
            binding.saveButton.text = "Save"
            binding.saveButton.icon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.check_24px)
        }
        itemToEdit?.let { populateFieldsForEdit(it) }

        // Set up gold rate from ViewModel
        lifecycleScope.launch {
            viewModel.lastUsedGoldRate.collect { lastRate ->
                if (binding.editViewAddress03.text.isNullOrEmpty()) {
                    binding.editViewAddress03.setText((lastRate * 10).toString())
                }
            }
        }
    }

    private fun setupFullScreenUI() {
        // Add any additional UI setup for full screen mode
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    private fun setupSpinners() {
        val jewelryItems = resources.getStringArray(R.array.listOfJewelleryItems)
        val karatItems = resources.getStringArray(R.array.listOfKt)
        val makingChargeTypes = resources.getStringArray(R.array.making_charge_types)

        binding.editViewJewelleryName03.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item,
                    jewelryItems
                )
            )
            setDropDownBackgroundResource(R.color.white)
        }


        binding.editViewKt.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item,
                    karatItems
                )
            )
            setDropDownBackgroundResource(R.color.white)
        }


        binding.makingChargeTypeSelector.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item,
                    makingChargeTypes
                )
            )
            setDropDownBackgroundResource(R.color.white)
        }


        // Set up making charge type change listener
        binding.makingChargeTypeSelector.setOnItemClickListener { _, _, _, _ ->
            updateMakingChargeHint()
        }


    }

    private fun updateMakingChargeHint() {
        val isPerGram = binding.makingChargeTypeSelector.text.toString() == "PER GRAM"
        binding.textInputViewMobilNumber03.hint =
            if (isPerGram) "Labour (per gram)" else "Labour  (fixed)"
    }


    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            if (viewModel.validateInputs(binding)) {
                val item: ItemModel = viewModel.createItemFromInputs(binding)
                if (editPosition != -1) {
                    onItemAdded?.invoke(item.copy(isExpanded = itemToEdit?.isExpanded ?: false))
                } else {
                    onItemAdded?.invoke(item)
                }
                dismiss()
            }
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun populateFieldsForEdit(item: ItemModel) {
        with(binding) {
            editViewJewelleryName03.setText(item.name)
            grosswtTextField.setText(item.grossWeight.toString())
            netswtTextField.setText(item.netWeight.toString())
            editViewMobilNumber03.setText((item.makingCharges).toString())
            editViewAddress03.setText((item.rateOfJewellery * 10).toString())
            stoneValueTextField.setText(item.stoneValue.toString())
            editViewKt.setText(item.karat)
            editViewWastage.setText(item.wastage.toString())
            makingChargeTypeSelector.setText(item.makingChargesType, false)
            updateMakingChargeHint()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(item: ItemModel? = null, position: Int = -1): ItemInputBottomSheet {
            return ItemInputBottomSheet().apply {
                itemToEdit = item
                editPosition = position
            }
        }
    }
}