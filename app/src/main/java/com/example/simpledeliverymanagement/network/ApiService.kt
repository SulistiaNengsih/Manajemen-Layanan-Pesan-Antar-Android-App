package com.example.simpledeliverymanagement.network

import com.example.simpledeliverymanagement.network.requests.LoginRequest
import com.example.simpledeliverymanagement.network.responses.GetOrderResponse
import com.example.simpledeliverymanagement.network.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Order Apis
    @POST("/user/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("/order/get_order")
    fun getOrder(): Call<GetOrderResponse>
}