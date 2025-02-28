package com.goldinvoice0.billingsoftware.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.goldinvoice0.billingsoftware.Model.Customer


@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: Customer) : Long

    @Delete
    suspend fun delete(customer: Customer)

    @Update
    suspend fun update(customer: Customer)

    @Query("SELECT * FROM customer_table")
    fun getAllCustomers(): LiveData<List<Customer>>
}