package com.example.simpledeliverymanagement.repositories

import com.example.simpledeliverymanagement.network.ApiService
import com.example.simpledeliverymanagement.network.requests.LoginRequest
import com.example.simpledeliverymanagement.network.responses.LoginResponse
import com.example.simpledeliverymanagement.network.RetrofitInstance
import com.example.simpledeliverymanagement.network.requests.ProcessOrderRequest
import com.example.simpledeliverymanagement.network.requests.UpdateCourierLocationRequest
import com.example.simpledeliverymanagement.network.responses.AddFcmResponse
import com.example.simpledeliverymanagement.network.responses.GetOrderByIdResponse
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

    fun getOrderById(orderId: Int, callback: (GetOrderByIdResponse?) -> Unit) {
        apiService.getOrderById(orderId).enqueue(object : Callback<GetOrderByIdResponse> {
            override fun onResponse(call: Call<GetOrderByIdResponse>, response: Response<GetOrderByIdResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GetOrderByIdResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun deliverOrder(orderId: Int, deliveredBy: String, callback: (GetOrderByIdResponse?) -> Unit) {
        var deliveredByRequest = ProcessOrderRequest()
        deliveredByRequest.deliveredBy = deliveredBy
        apiService.deliverOrder(orderId, deliveredByRequest).enqueue(object : Callback<GetOrderByIdResponse> {
            override fun onResponse(call: Call<GetOrderByIdResponse>, response: Response<GetOrderByIdResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GetOrderByIdResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun completeOrder(orderId: Int, callback: (GetOrderByIdResponse?) -> Unit) {
        apiService.completeOrder(orderId).enqueue(object : Callback<GetOrderByIdResponse> {
            override fun onResponse(call: Call<GetOrderByIdResponse>, response: Response<GetOrderByIdResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GetOrderByIdResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun addFcmToken(fcmToken: String, callback: (AddFcmResponse?) -> Unit) {
        apiService.addFcmToken(fcmToken).enqueue(object : Callback<AddFcmResponse> {
            override fun onResponse(call: Call<AddFcmResponse>, response: Response<AddFcmResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<AddFcmResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun updateCourierLocation(orderDeliveryId: Int, latitude: String, longitude: String, callback: (GetOrderByIdResponse?) -> Unit) {
        var request = UpdateCourierLocationRequest()
        request.latitude = latitude;
        request.longitude = longitude;

        apiService.updateCourierLocation(orderDeliveryId, request).enqueue(object : Callback<GetOrderByIdResponse> {
            override fun onResponse(call: Call<GetOrderByIdResponse>, response: Response<GetOrderByIdResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GetOrderByIdResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}