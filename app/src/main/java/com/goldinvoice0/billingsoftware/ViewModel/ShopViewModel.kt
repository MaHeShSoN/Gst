package com.goldinvoice0.billingsoftware.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.goldinvoice0.billingsoftware.DadatBase.ShopDatabase
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.Repository.ShopRepository
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopRepository
    val shop: LiveData<Shop>

    init {
        val shopDao = ShopDatabase.getDatabase(application).shopDao()
        repository = ShopRepository(shopDao)
        shop = repository.shop
    }

    fun insert(shop: Shop) = viewModelScope.launch {
        repository.insert(shop)
    }

    fun update(shop: Shop) = viewModelScope.launch {
        repository.update(shop)
    }

}