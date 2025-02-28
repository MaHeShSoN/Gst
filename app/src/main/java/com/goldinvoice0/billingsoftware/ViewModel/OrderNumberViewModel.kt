package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.goldinvoice0.billingsoftware.DadatBase.InvoiceNumberDatabase
import com.goldinvoice0.billingsoftware.DadatBase.OrderNumberDatabase
import com.goldinvoice0.billingsoftware.Repository.InvoiceNumberRepository
import com.goldinvoice0.billingsoftware.Repository.OrderNumberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



/**
 * ViewModel for managing order numbers in the billing software.
 * Provides functionality to retrieve the next order number and count.
 */
class OrderNumberViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrderNumberRepository

    init {
        val dao = OrderNumberDatabase.getDatabase(application).orderNumberDao()
        repository = OrderNumberRepository(dao)
    }

    /**
     * Retrieves the next available order number.
     * @return The next order number as [Long].
     */
    suspend fun getNextOrderNumber(): Long {
        return withContext(Dispatchers.IO) {
            repository.getNextOrderNumber()
        }
    }

    /**
     * Gets the current order count in the database.
     * @return The total count of orders as [Int].
     */
    suspend fun getCurrentCount(): Int {
        return withContext(Dispatchers.IO) {
            repository.getCurrentCount()
        }
    }
}
