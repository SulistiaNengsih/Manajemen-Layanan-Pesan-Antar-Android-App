package com.example.simpledeliverymanagement.repositories

import com.example.simpledeliverymanagement.network.ApiService
import com.example.simpledeliverymanagement.network.requests.LoginRequest
import com.example.simpledeliverymanagement.network.responses.LoginResponse
import com.example.simpledeliverymanagement.network.RetrofitInstance
import com.example.simpledeliverymanagement.network.responses.GetOrderResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderRepository {
    private val apiService: ApiService = RetrofitInstance.apiService

    fun getOrder(callback: (GetOrderResponse?) -> Unit) {
        apiService.getOrder().enqueue(object : Callback<GetOrderResponse> {
            override fun onResponse(call: Call<GetOrderResponse>, response: Response<GetOrderResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GetOrderResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}