package com.goldinvoice0.billingsoftware

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.Payment
import com.goldinvoice0.billingsoftware.Model.PaymentRecived
import com.goldinvoice0.billingsoftware.Model.PaymentTransaction


class SharedViewModel : ViewModel() {

    private val orderData = MutableLiveData<Order>()

    private val _payments = MutableLiveData<MutableList<Payment>>(mutableListOf())
    val payments: LiveData<MutableList<Payment>> = _payments
    var paymentsDeleted = false // New flag to track deletion

    private val _jewelleryItem = MutableLiveData<MutableList<JewelryItem>>(mutableListOf())
    val jewelleryItems: LiveData<MutableList<JewelryItem>> = _jewelleryItem
    var jewelleryItemDeleted = false // New flag to track deletion

    private val _paymentsAdvance = MutableLiveData<List<PaymentTransaction>>()
    val paymentsAdvance: LiveData<List<PaymentTransaction>> = _paymentsAdvance

    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    init {
        _paymentsAdvance.value = emptyList()
        _total.value = 0.0
    }

    fun addPaymentAdvance(payment: PaymentTransaction) {
        val currentList = _paymentsAdvance.value?.toMutableList() ?: mutableListOf()
        currentList.add(payment)
        _paymentsAdvance.value = currentList
        updateTotalAdvance()
    }

    fun removePaymentAdvance(payment: PaymentTransaction) {
        val currentList = _paymentsAdvance.value?.toMutableList() ?: mutableListOf()
        currentList.remove(payment)
        _paymentsAdvance.value = currentList
        updateTotalAdvance()
    }

    private fun updateTotalAdvance() {
        val newTotal = _paymentsAdvance.value?.sumOf { it.amount } ?: 0.0
        _total.value = newTotal
    }

    // MutableLiveData for the list of payments
    private val _paymentEntry =
        MutableLiveData<MutableList<PaymentRecived>>().apply { value = mutableListOf() }
    val paymentEntry: LiveData<MutableList<PaymentRecived>> get() = _paymentEntry


    // Add a flag to track changes
    private val _hasChangesPayments = MutableLiveData<Boolean>(false)
    val hasChangesPayments: LiveData<Boolean> = _hasChangesPayments


    fun resetChanges() {
        _hasChangesPayments.value = false
    }


    // Add a new payment to the list
    fun addPaymentRecived(payment: PaymentRecived) {
        val updatedpaymentEntry = _paymentEntry.value ?: mutableListOf()
        updatedpaymentEntry.add(payment)
        _paymentEntry.value = updatedpaymentEntry // Notify observers of the updated list
        _hasChangesPayments.value = true
    }

    // Remove a payment from the list
    fun deletePaymentRecived(payment: PaymentRecived) {
        val updatedpaymentEntry = _paymentEntry.value ?: mutableListOf()
        updatedpaymentEntry.remove(payment)
        _paymentEntry.value = updatedpaymentEntry // Notify observers of the updated list
        _hasChangesPayments.value = true
    }

    fun resetPaymentChanges() {
        _hasChangesPayments.value = false
    }


    fun initializePaymentList(payments: List<PaymentRecived>) {
        _paymentEntry.value = payments.toMutableList() // Creating a new instance to notify observers
    }


    fun addPayment(payment: Payment) {
        val currentPayments = _payments.value ?: mutableListOf()
        currentPayments.add(payment)
        _payments.value = currentPayments // Important: Assign the updated list
    }

    fun addJewelleryItem(jewelleryItem: JewelryItem) {
        val currentPayments = _jewelleryItem.value ?: mutableListOf()
        currentPayments.add(jewelleryItem)
        _jewelleryItem.value = currentPayments // Important: Assign the updated list
    }


    fun removePayment(position: Int) {
        val currentPayments = _payments.value ?: mutableListOf()
        if (position in 0 until currentPayments.size) {
            currentPayments.removeAt(position)
            _payments.value = currentPayments // Important: Assign the updated list
        }
        if (currentPayments.isEmpty()) {
            paymentsDeleted = true // Mark that payments were deleted
        }
    }

    fun removeJewelryItem(position: Int) {
        val currentJewelryItem = _jewelleryItem.value ?: mutableListOf()
        if (position in 0 until currentJewelryItem.size) {
            currentJewelryItem.removeAt(position)
            _jewelleryItem.value = currentJewelryItem // Important: Assign the updated list
        }
        if (currentJewelryItem.isEmpty()) {
            jewelleryItemDeleted = true // Mark that payments were deleted
        }
    }


    fun updateJewelryItem(position: Int, updatedItem: JewelryItem) {
        val currentJewelryItems = _jewelleryItem.value ?: mutableListOf()
        if (position in 0 until currentJewelryItems.size) {
            currentJewelryItems[position] = updatedItem
            _jewelleryItem.value = currentJewelryItems // Update LiveData
        } else {
            Log.e("SharedViewModel", "Invalid position: $position for jewelry item update")
        }
    }


    fun getPaymentsList(): MutableList<Payment> {
        return _payments.value ?: mutableListOf()
    }

    fun getJewelryItemList(): MutableList<JewelryItem> {
        return _jewelleryItem.value ?: mutableListOf()
    }


    fun clearPayments() {
        _payments.value = mutableListOf()
    }
    fun clearPaymentsAdvance() {
        _paymentsAdvance.value = mutableListOf()
    }

    fun clearRevivedPayments(){
        _paymentEntry.value = mutableListOf()
    }

    fun clearJewelryItem() {
        _jewelleryItem.value = mutableListOf()
    }


    fun setOrder(order: Order) {
        orderData.value = order
    }


    fun getOrderData(): LiveData<Order> {
        return orderData
    }


}



