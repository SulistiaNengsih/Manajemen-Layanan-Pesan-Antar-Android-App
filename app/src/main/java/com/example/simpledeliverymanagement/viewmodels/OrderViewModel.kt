package com.example.simpledeliverymanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simpledeliverymanagement.network.responses.AddFcmResponse
import com.example.simpledeliverymanagement.network.responses.GetOrderByIdResponse
import com.example.simpledeliverymanagement.network.responses.GetOrderResponse
import com.example.simpledeliverymanagement.repositories.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val orderRepository = OrderRepository()
    private val _orderResult = MutableLiveData<GetOrderResponse?>()
    private val _getOrderByIdResult = MutableLiveData<GetOrderByIdResponse?>()
    private val _addFcmResponseResult = MutableLiveData<AddFcmResponse?>()
    val orderResult: LiveData<GetOrderResponse?> = _orderResult
    val getOrderByIdResult: LiveData<GetOrderByIdResponse?> = _getOrderByIdResult
    val addFcmResult: LiveData<AddFcmResponse?> = _addFcmResponseResult
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

    fun getOrderById(orderId: Int) {
        orderRepository.getOrderById(orderId) { response ->
            _getOrderByIdResult.postValue(response)
        }
    }

    fun deliverOrder(orderId: Int, deliveredBy: String) {
        orderRepository.deliverOrder(orderId, deliveredBy) { response ->
            _getOrderByIdResult.postValue(response)
        }
    }

    fun completeOrder(orderId: Int) {
        orderRepository.completeOrder(orderId) { response ->
            _getOrderByIdResult.postValue(response)
        }
    }

    fun addFcmToken(fcmToken: String) {
        orderRepository.addFcmToken(fcmToken) { response ->
            _addFcmResponseResult.postValue(response)
        }
    }

    fun updateCourierLocation(orderDeliveryId: Int, latitude: String, longitude: String) {
        orderRepository.updateCourierLocation(orderDeliveryId, latitude, longitude) { response ->
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}