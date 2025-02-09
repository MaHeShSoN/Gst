package com.goldinvoice0.billingsoftware.Repository

import com.goldinvoice0.billingsoftware.Dao.OrderNumberDao
import com.goldinvoice0.billingsoftware.Model.OrderNumber

class OrderNumberRepository(private val orderNumberDao: OrderNumberDao) {

    suspend fun getNextOrderNumber(): Long {
        val lastorder = orderNumberDao.getLastOrderNumber()
        val nextNumber = (lastorder?.number ?: 0) + 1
        orderNumberDao.insert(OrderNumber(number = nextNumber))
        return nextNumber
    }

    suspend fun getCurrentCount(): Int = orderNumberDao.getCount()
}