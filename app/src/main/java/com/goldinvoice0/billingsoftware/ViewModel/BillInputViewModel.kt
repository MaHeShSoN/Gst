package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.goldinvoice0.billingsoftware.DadatBase.BillInputDatabase
import com.goldinvoice0.billingsoftware.Model.BillInputs
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.Repository.BillInputsRepository
import kotlinx.coroutines.launch


class BillInputViewModel(application: Application) : AndroidViewModel(application) {
    private val billInputRepository: BillInputsRepository
    private val _customerSelected = MutableLiveData<Customer?>()
    val customerSelected: LiveData<Customer?> get() = _customerSelected


    fun setCustomer(customer: Customer?) {
        _customerSelected.value = customer
    }

    fun clearCustomer() {
        _customerSelected.value = null
    }



    init {
        val billInputDao = BillInputDatabase.getDatabase(application).billInputDao() // Get DB instance
        billInputRepository = BillInputsRepository(billInputDao)
    }

    fun getAllBillInput(): LiveData<List<BillInputs>> {
        lateinit var allBillInputs: LiveData<List<BillInputs>>
        viewModelScope.launch {
            allBillInputs = billInputRepository.getAllBillInput()
        }
        return allBillInputs

    }

    fun insertBillInputs(billInputs: BillInputs) = viewModelScope.launch {
        billInputRepository.insertBillInputs(billInputs)
    }

    fun getBillInputsById(billInputsId: Int): LiveData<BillInputs?> {
        return liveData {
            emit(billInputRepository.getBillInputById(billInputsId))
        }
    }

    fun updateBillInputs(billInputs: BillInputs) = viewModelScope.launch {
        billInputRepository.updateBillInput(billInputs)
    }

    fun deleteBillInputs(billInputs: BillInputs) = viewModelScope.launch {
        billInputRepository.deleteOrder(billInputs)
    }

    fun deleteBillInputsById(billId: Int) = viewModelScope.launch {
        billInputRepository.deleteBillInputById(billId)
    }

}