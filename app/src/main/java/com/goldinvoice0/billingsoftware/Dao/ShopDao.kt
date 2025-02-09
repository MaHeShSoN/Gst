package com.goldinvoice0.billingsoftware.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.goldinvoice0.billingsoftware.Model.Shop

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shop: Shop)

    @Query("SELECT * FROM shop_table LIMIT 1")
    fun getShop(): LiveData<Shop>

    @Update
    suspend fun update(shop: Shop)
}