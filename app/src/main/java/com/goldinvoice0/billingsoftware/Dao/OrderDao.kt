package com.goldinvoice0.billingsoftware.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.goldinvoice0.billingsoftware.Model.Order

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM order_table_")
    fun getAllOrders(): LiveData<List<Order>>

    // Add other queries as needed (e.g., get by ID, filter by status, etc.)
    @Query("SELECT * FROM order_table_ WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): Order?


    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

}