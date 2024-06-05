package com.example.simpledeliverymanagement.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://1229-182-1-82-239.ngrok-free.app"
    private const val MAP_BASE_URL = "https://maps.googleapis.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val mapApiService: MapApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MAP_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapApiService::class.java)
    }
}