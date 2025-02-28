package com.goldinvoice0.billingsoftware.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedNavigationViewModel : ViewModel() {
    private val _requestedTab = MutableLiveData<Int>()
    val requestedTab: LiveData<Int> = _requestedTab

    fun requestTab(position: Int) {
        _requestedTab.value = position
    }
}
