package com.goldinvoice0.billingsoftware.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldinvoice0.billingsoftware.Model.InvoiceNumber
import com.goldinvoice0.billingsoftware.Model.OrderNumber

@Dao
interface OrderNumberDao {
    @Query("SELECT * FROM order_numbers ORDER BY id DESC LIMIT 1")
    suspend fun getLastOrderNumber(): OrderNumber?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderNumber: OrderNumber)

    @Query("SELECT COUNT(*) FROM order_numbers")
    suspend fun getCount(): Int
}