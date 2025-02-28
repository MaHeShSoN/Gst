package com.goldinvoice0.billingsoftware.Repository

import androidx.lifecycle.LiveData
import com.goldinvoice0.billingsoftware.Dao.CustomerDao
import com.goldinvoice0.billingsoftware.Model.Customer

class CustomerRepository(private val customerDao: CustomerDao) {
    val allCustomers: LiveData<List<Customer>> = customerDao.getAllCustomers()

    suspend fun insert(customer: Customer) : Long {
        return customerDao.insert(customer)
    }
    suspend fun delete(customer: Customer){
        customerDao.delete(customer)
    }
    suspend fun update(customer: Customer){
        customerDao.update(customer)
    }
}