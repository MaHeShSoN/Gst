package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.goldinvoice0.billingsoftware.DadatBase.OrderDatabase
import com.goldinvoice0.billingsoftware.Model.Order
import com.goldinvoice0.billingsoftware.Repository.OrderRepository
import kotlinx.coroutines.launch


class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val orderRepository: OrderRepository

    init {
        val orderDao = OrderDatabase.getDatabase(application).orderDao() // Get DB instance
        orderRepository = OrderRepository(orderDao)
    }

    fun getAllOrders(): LiveData<List<Order>> {
        lateinit var allOrders: LiveData<List<Order>>
        viewModelScope.launch {
            allOrders = orderRepository.getAllOrders()
        }
        return allOrders

    }

    fun insertOrder(order: Order) = viewModelScope.launch {
        orderRepository.insertOrder(order)
    }

    fun getOrderById(orderId: Int): LiveData<Order?> {
        return liveData {
            emit(orderRepository.getOrderById(orderId))
        }
    }

    fun updateOrder(order: Order) = viewModelScope.launch {
        orderRepository.updateOrder(order)
    }

    fun deleteOrder(order: Order) = viewModelScope.launch {
        orderRepository.deleteOrder(order)
    }
}


