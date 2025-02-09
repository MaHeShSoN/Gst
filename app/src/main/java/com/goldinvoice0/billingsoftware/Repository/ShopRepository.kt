package com.goldinvoice0.billingsoftware.Repository

import androidx.lifecycle.LiveData
import com.goldinvoice0.billingsoftware.Dao.ShopDao
import com.goldinvoice0.billingsoftware.Model.Shop

class ShopRepository(private val shopDao: ShopDao) {

    val shop: LiveData<Shop> = shopDao.getShop()

    suspend fun insert(shop: Shop) {
        shopDao.insert(shop)
    }

    suspend fun update(shop: Shop) {
        shopDao.update(shop)
    }
}