package com.goldinvoice0.billingsoftware.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldinvoice0.billingsoftware.Model.InvoiceNumber

@Dao
interface InvoiceNumberDao {
    @Query("SELECT * FROM invoice_numbers ORDER BY id DESC LIMIT 1")
    suspend fun getLastInvoiceNumber(): InvoiceNumber?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invoiceNumber: InvoiceNumber)

    @Query("SELECT COUNT(*) FROM invoice_numbers")
    suspend fun getCount(): Int
}