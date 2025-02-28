

package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.goldinvoice0.billingsoftware.DadatBase.CustomerDatabase
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.Repository.CustomerRepository
import kotlinx.coroutines.launch

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CustomerRepository
    val allCustomers: LiveData<List<Customer>>

    private val _insertedCustomer = MutableLiveData<Customer?>() // MutableLiveData for inserted customer
    val insertedCustomer: LiveData<Customer?> = _insertedCustomer // Public LiveData

    // Add navigation state variables
    private val _navigationSource = MutableLiveData<String?>()
    val navigationSource: LiveData<String?> = _navigationSource

    private val _navigationAction = MutableLiveData<Int?>()
    val navigationAction: LiveData<Int?> = _navigationAction

    private val _selectionMode = MutableLiveData(false)
    val selectionMode: LiveData<Boolean> = _selectionMode

    // Methods to set navigation state
    fun setNavigationParams(sourceId: String?, action: Int?, selection: Boolean) {
        _navigationSource.value = sourceId
        _navigationAction.value = action
        _selectionMode.value = selection
        Log.d("customerList", "selected Mode2 "+_selectionMode.value.toString()+
                " navigationSource2 ${_navigationSource.value}"+" navigationAction2 ${_navigationAction.value}")
    }
    fun clearInsertedCustomer() {
        _insertedCustomer.value = null
    }

    init {
        val customerDao = CustomerDatabase.getDatabase(application).customerDao()
        repository = CustomerRepository(customerDao)
        allCustomers = repository.allCustomers
    }

    fun insert(customer: Customer) { // Changed to not return anything directly
        viewModelScope.launch {
            val id = repository.insert(customer)
            _insertedCustomer.postValue(customer.copy(id = id.toInt())) // Post the updated customer
        }
    }
    fun update(customer: Customer) { // Changed to not return anything directly
        viewModelScope.launch {
            repository.update(customer)
//            _insertedCustomer.postValue(customer.copy(id = id.toInt())) // Post the updated customer
        }
    }

    fun delete(customer: Customer) = viewModelScope.launch {
        repository.delete(customer)
    }
}
