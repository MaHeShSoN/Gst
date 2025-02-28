package com.goldinvoice0.billingsoftware.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.goldinvoice0.billingsoftware.Model.BillInputs

@Dao
interface BillsInputDao {
    @Insert
    suspend fun insertBillInput(billInput: BillInputs)

    @Query("SELECT * FROM billInputs_table_")
    fun getAllBillInputs(): LiveData<List<BillInputs>>

    // Add other queries as needed (e.g., get by ID, filter by status, etc.)
    @Query("SELECT * FROM BillInputs_table_ WHERE id = :billInputsId")
    suspend fun getBillInputsById(billInputsId: Int): BillInputs?


    @Update
    suspend fun updateBillInput(billInput: BillInputs)

    @Delete
    suspend fun deleteBillInputs(billInput: BillInputs)

    @Query("DELETE FROM BillInputs_table_ WHERE id = :billId")
    suspend fun deleteBillById(billId: Int)




}