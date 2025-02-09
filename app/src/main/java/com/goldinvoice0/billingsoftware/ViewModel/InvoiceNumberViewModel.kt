package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.goldinvoice0.billingsoftware.DadatBase.InvoiceNumberDatabase
import com.goldinvoice0.billingsoftware.Repository.InvoiceNumberRepository


// InvoiceNumberViewModel.kt - ViewModel
class InvoiceNumberViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InvoiceNumberRepository

    init {
        val dao = InvoiceNumberDatabase.getDatabase(application).invoiceNumberDao()
        repository = InvoiceNumberRepository(dao)
    }

    suspend fun getNextInvoiceNumber(): Long = repository.getNextInvoiceNumber()

    suspend fun getCurrentCount(): Int = repository.getCurrentCount()
}