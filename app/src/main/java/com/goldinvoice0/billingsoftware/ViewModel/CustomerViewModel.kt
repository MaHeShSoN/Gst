package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.goldinvoice0.billingsoftware.DadatBase.CustomerDatabase
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.Repository.CustomerRepository
import kotlinx.coroutines.launch


class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CustomerRepository
    val allCustomers: LiveData<List<Customer>>

    init {
        val customerDao = CustomerDatabase.getDatabase(application).customerDao()
        repository = CustomerRepository(customerDao)
        allCustomers = repository.allCustomers
    }

    fun insert(customer: Customer) = viewModelScope.launch {
        repository.insert(customer)
    }
    fun delete(customer: Customer) = viewModelScope.launch {
        repository.delete(customer)
    }
}