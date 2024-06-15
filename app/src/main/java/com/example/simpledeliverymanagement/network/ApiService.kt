package com.example.simpledeliverymanagement.network

import com.example.simpledeliverymanagement.network.requests.LoginRequest
import com.example.simpledeliverymanagement.network.requests.ProcessOrderRequest
import com.example.simpledeliverymanagement.network.requests.UpdateCourierLocationRequest
import com.example.simpledeliverymanagement.network.responses.AddFcmResponse
import com.example.simpledeliverymanagement.network.responses.GetOrderByIdResponse
import com.example.simpledeliverymanagement.network.responses.GetOrderResponse
import com.example.simpledeliverymanagement.network.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Order Apis
    @POST("/user/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/order/deliver_order/{orderId}")
    fun deliverOrder(@Path("orderId") orderId: Int, @Body deliveredBy: ProcessOrderRequest): Call<GetOrderByIdResponse>

    @POST("/order/complete_order/{orderId}")
    fun completeOrder(@Path("orderId") orderId: Int): Call<GetOrderByIdResponse>

    @POST("/order/add_fcm_token")
    fun addFcmToken(@Body fcmToken: String): Call<AddFcmResponse>

    @POST("/order/update_courrier_location/{orderDeliveryId}")
    fun updateCourierLocation(@Path("orderDeliveryId") orderDeliveryId: Int, @Body latlng: UpdateCourierLocationRequest): Call<GetOrderByIdResponse>

    @GET("/order/get_order")
    fun getOrder(): Call<GetOrderResponse>

    @GET("/order/get_order/{orderId}")
    fun getOrderById(@Path("orderId") orderId: Int): Call<GetOrderByIdResponse>

}