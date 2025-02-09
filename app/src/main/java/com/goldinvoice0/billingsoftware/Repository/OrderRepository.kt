package com.goldinvoice0.billingsoftware.Repository

import androidx.lifecycle.LiveData
import com.goldinvoice0.billingsoftware.Dao.OrderDao
import com.goldinvoice0.billingsoftware.Model.Order

class OrderRepository(private val orderDao: OrderDao) {
    suspend fun insertOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    fun getAllOrders(): LiveData<List<Order>> {
        return orderDao.getAllOrders()
    }

    suspend fun getOrderById(orderId: Int): Order? {
        return orderDao.getOrderById(orderId)
    }

    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }

    suspend fun deleteOrder(order: Order) {
        orderDao.deleteOrder(order)
    }
}