package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.goldinvoice0.billingsoftware.DadatBase.InvoiceNumberDatabase
import com.goldinvoice0.billingsoftware.DadatBase.OrderNumberDatabase
import com.goldinvoice0.billingsoftware.Repository.InvoiceNumberRepository
import com.goldinvoice0.billingsoftware.Repository.OrderNumberRepository

class OrderNumberViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: OrderNumberRepository

    init {
        val dao = OrderNumberDatabase.getDatabase(application).orderNumberDao()
        repository = OrderNumberRepository(dao)
    }

    suspend fun getNextOrderNumber(): Long = repository.getNextOrderNumber()

    suspend fun getCurrentCount(): Int = repository.getCurrentCount()
}