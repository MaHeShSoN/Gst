package com.goldinvoice0.billingsoftware.Repository

import com.goldinvoice0.billingsoftware.Dao.InvoiceNumberDao
import com.goldinvoice0.billingsoftware.Model.InvoiceNumber


// InvoiceNumberRepository.kt - Repository
class InvoiceNumberRepository(private val invoiceNumberDao: InvoiceNumberDao) {

    suspend fun getNextInvoiceNumber(): Long {
        val lastInvoice = invoiceNumberDao.getLastInvoiceNumber()
        val nextNumber = (lastInvoice?.number ?: 0) + 1
        invoiceNumberDao.insert(InvoiceNumber(number = nextNumber))
        return nextNumber
    }

    suspend fun getCurrentCount(): Int = invoiceNumberDao.getCount()
}