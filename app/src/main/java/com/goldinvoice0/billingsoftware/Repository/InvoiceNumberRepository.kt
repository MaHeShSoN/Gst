package com.goldinvoice0.billingsoftware.Repository

import com.goldinvoice0.billingsoftware.Dao.InvoiceNumberDao
import com.goldinvoice0.billingsoftware.Model.InvoiceNumber
import java.util.Calendar


// InvoiceNumberRepository.kt - Repository
class InvoiceNumberRepository(private val invoiceNumberDao: InvoiceNumberDao) {

//    suspend fun getNextInvoiceNumber(): Long {
//        val lastInvoice = invoiceNumberDao.getLastInvoiceNumber()
//        val nextNumber = (lastInvoice?.number ?: 0) + 1
//        invoiceNumberDao.insert(InvoiceNumber(number = nextNumber))
//        return nextNumber
//    }

    suspend fun getNextInvoiceNumber(): Long {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) // Get the current year
        val lastInvoice = invoiceNumberDao.getLastInvoiceNumber()

        val lastYear = lastInvoice?.number?.toString()?.take(4)?.toIntOrNull() ?: currentYear
        val lastNumber = lastInvoice?.number?.toString()?.drop(4)?.toIntOrNull() ?: 0

        val nextNumber = if (lastYear == currentYear) lastNumber + 1 else 1 // Reset if year changes

        // Format: "YYYY001", "YYYY002", etc.
        val formattedInvoice = "%d%03d".format(currentYear, nextNumber).toLong()

        invoiceNumberDao.insert(InvoiceNumber(number = formattedInvoice))
        return formattedInvoice
    }


    suspend fun getCurrentCount(): Int = invoiceNumberDao.getCount()
}