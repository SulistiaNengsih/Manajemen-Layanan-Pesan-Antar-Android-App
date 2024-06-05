package com.example.simpledeliverymanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simpledeliverymanagement.network.responses.GetOrderResponse
import com.example.simpledeliverymanagement.repositories.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val orderRepository = OrderRepository()
    private val _orderResult = MutableLiveData<GetOrderResponse>()
    val orderResult: LiveData<GetOrderResponse?> = _orderResult
    private var fetchJob: Job? = null

    init {
        startPeriodicFetch()
    }

    private fun startPeriodicFetch() {
        fetchJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                getOrder()
                delay(10000) // akses data setiap 10 detik
            }
        }
    }

    fun getOrder() {
        orderRepository.getOrder() { response ->
            _orderResult.postValue(response)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}