package com.example.simpledeliverymanagement.network

import com.example.simpledeliverymanagement.network.responses.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApiService {
    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Call<DirectionsResponse>
}