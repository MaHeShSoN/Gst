package com.goldinvoice0.billingsoftware


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goldinvoice0.billingsoftware.Model.ItemModel
import com.goldinvoice0.billingsoftware.databinding.BottomSheetItemInputBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class JewelryViewModel : ViewModel() {
    private val _items = MutableLiveData<List<ItemModel>>(emptyList())
    val items: LiveData<List<ItemModel>> = _items

    private val _totalValue = MutableLiveData<Long>(0)
    val totalValue: LiveData<Long> = _totalValue

    // Add a flag to track changes
    private val _hasChanges = MutableLiveData<Boolean>(false)
    val hasChanges: LiveData<Boolean> = _hasChanges

    private val _lastUsedGoldRate = MutableStateFlow<Int>(0)
    val lastUsedGoldRate: StateFlow<Int> = _lastUsedGoldRate.asStateFlow()

    fun updateLastUsedGoldRate(newRate: Int) {
        _lastUsedGoldRate.value = newRate
    }

    fun resetChanges() {
        _hasChanges.value = false
    }


    fun addItem(item: ItemModel) {
        val currentList = _items.value.orEmpty().toMutableList()
        currentList.add(item)
        _items.value = currentList
        updateTotalValue()
        _hasChanges.value = true
    }

    fun updateItem(position: Int, item: ItemModel) {
        val currentList = _items.value.orEmpty().toMutableList()
        if (position in currentList.indices) {
            currentList[position] = item
            _items.value = currentList
            updateTotalValue()
        }
        _hasChanges.value = true
    }

    fun removeItem(position: Int) {
        val currentList = _items.value.orEmpty().toMutableList()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _items.value = currentList
            updateTotalValue()
        }
        _hasChanges.value = true

    }


    fun initializeItemList(items: List<ItemModel>) {
        _items.value = items
    }

    private fun updateTotalValue() {
        val total = _items.value.orEmpty().sumOf { it.totalValue }
        _totalValue.value = total
    }

    fun calculateTotalValue(
        netWeight: Float,
        wastage: Float,
        goldPrice: Int,
        makingCharges: Int,
        pieces: Int,
        stoneValue: Int
    ): Long {
        return ((((netWeight + wastage) * goldPrice.toFloat()) + makingCharges) * pieces.toFloat() + stoneValue).toLong()
    }

    fun calculateMakingCharges(
        netWeight: Double,
        makingChargeInput: Int,
        isPerGram: Boolean
    ): Int {
        return if (isPerGram) {
            Log.d("mackingCharge", isPerGram.toString())
            (netWeight * makingChargeInput).toInt()
        } else {
            Log.d("mackingCharge", isPerGram.toString() + " in the else")
            makingChargeInput
        }
    }

    fun validateInputs(binding: BottomSheetItemInputBinding): Boolean {
        // Implement validation logic
        var isValid = true
        with(binding) {
            if (editViewJewelleryName03.text.isNullOrBlank()) {
                editViewJewelleryName03.error = "Required"
                isValid = false
            }
            if (editViewAddress03.text.isNullOrBlank()) {
                textInputViewAddress03.error = "Required"
                isValid = false
            }
            if (editViewWastage.text.isNullOrBlank()) {
                textInputViewWastage.error = "Required"
                isValid = false
            }
            if (editViewMobilNumber03.text.isNullOrBlank()) {
                textInputViewMobilNumber03.error = "Required"
                isValid = false
            }
            if (editViewKt.text.isNullOrBlank()) {
                textInputKt.error = "Required"
                isValid = false
            }

            if (editViewpice.text.isNullOrBlank()) {
                textInputViewpice.error = "Required"
                isValid = false
            }

            if (stoneValueTextField.text.isNullOrBlank()) {
                stoneValueTextInputLayotu.error = "Required"
                isValid = false
            }

            if (netswtTextField.text.isNullOrBlank()) {
                netwtTextInputLayotu.error = "Required"
                isValid = false
            }

            if (grosswtTextField.text.isNullOrBlank()) {
                grosswtTextInputLayotu.error = "Required"
                isValid = false
            }

            // Add similar validation for other fields
            // Existing validation logic...
            val netWeight = binding.netswtTextField.text.toString().toDoubleOrNull() ?: 0.0
            val makingCharge = binding.editViewMobilNumber03.text.toString().toIntOrNull() ?: 0

            // Additional validation for making charges based on type
            val isPerGram = binding.makingChargeTypeSelector.text.toString() == "PER GRAM"
            if (isPerGram && (makingCharge <= 0 || netWeight <= 0)) {
                binding.editViewMobilNumber03.error = "Invalid making charge or net weight"
                return false
            }


        }
        return isValid
    }

    fun createItemFromInputs(binding: BottomSheetItemInputBinding): ItemModel {
        Log.d("mackingCharge", "1")
        with(binding) {
            val name = editViewJewelleryName03.text.toString()
            val grossWeight = grosswtTextField.text.toString().toFloat()
            val netWeight = netswtTextField.text.toString().toFloat()
            val goldPrice = editViewAddress03.text.toString().toInt() / 10
            val stoneValue = stoneValueTextField.text.toString().toInt()
            val makingCharges = editViewMobilNumber03.text.toString().toInt()
            val pieces = editViewpice.text.toString().toInt()
            val karat = editViewKt.text.toString()
            val wastage = editViewWastage.text.toString().toFloat()


            val netWeightFinal = netswtTextField.text.toString().toDoubleOrNull() ?: 0.0
            val makingChargeInput = editViewMobilNumber03.text.toString().toIntOrNull() ?: 0
            val isPerGram = makingChargeTypeSelector.text.toString() == "PER GRAM"

            val finalMakingCharge =
                calculateMakingCharges(netWeightFinal, makingChargeInput, isPerGram)

            var makingChargeType = if (isPerGram) "PER GRAM" else "FIX"

            val totalValue = calculateTotalValue(
                netWeight, wastage, goldPrice, finalMakingCharge, pieces, stoneValue
            )

            //Update the lastGoldRate
            val currentGoldRate =
                binding.editViewAddress03.text.toString().toIntOrNull()?.div(10) ?: 0

            // Update last used gold rate if different
            if (currentGoldRate != lastUsedGoldRate.value) {
                updateLastUsedGoldRate(currentGoldRate)
            }

            return ItemModel(
                name = name,
                grossWeight = grossWeight,
                netWeight = netWeight,
                rateOfJewellery = goldPrice,
                stoneValue = stoneValue,
                finalMakingCharges = finalMakingCharge * pieces,
                makingCharges = makingCharges,
                makingChargesType = makingChargeType,
                totalValue = totalValue,
                karat = karat,
                wastage = wastage,
                piece = pieces
            )
        }
    }

    fun clearItems() {
        _items.value = emptyList()
    }
}