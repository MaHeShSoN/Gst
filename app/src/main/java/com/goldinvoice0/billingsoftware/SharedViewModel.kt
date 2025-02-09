package com.goldinvoice0.billingsoftware

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goldinvoice0.billingsoftware.Model.JewelryItem
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.Payment
//import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Model.PdfData
import com.goldinvoice0.billingsoftware.Model.PdfFinalData


class SharedViewModel : ViewModel() {

    private val pdfData = MutableLiveData<PdfData>()
    private val pdfFinalData = MutableLiveData<PdfFinalData>()
    private val orderData = MutableLiveData<Order>()

    private val _payments = MutableLiveData<MutableList<Payment>>(mutableListOf())
    val payments: LiveData<MutableList<Payment>> = _payments
    var paymentsDeleted = false // New flag to track deletion

    private val _jewelleryItem = MutableLiveData<MutableList<JewelryItem>>(mutableListOf())
    val jewelleryItems: LiveData<MutableList<JewelryItem>> = _jewelleryItem
    var jewelleryItemDeleted = false // New flag to track deletion

    fun addPayment(payment: Payment) {
        val currentPayments = _payments.value ?: mutableListOf()
        currentPayments.add(payment)
        _payments.value = currentPayments // Important: Assign the updated list
    }

    fun addJewelleryItem(jewelleryItem: JewelryItem){
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
    fun clearJewelryItem() {
        _jewelleryItem.value = mutableListOf()
    }

    fun setPdfData(data: PdfData) {
        pdfData.value = data
    }

    fun setReceivedList(list: PdfFinalData) {
        pdfFinalData.value = list
    }
    fun setOrder(order: Order) {
        orderData.value = order
    }


    fun getPdfData(): LiveData<PdfData> {
        return pdfData
    }
    fun getOrderData(): LiveData<Order> {
        return orderData
    }

    fun getReceivedList(): LiveData<PdfFinalData> {
        return pdfFinalData
    }

}
