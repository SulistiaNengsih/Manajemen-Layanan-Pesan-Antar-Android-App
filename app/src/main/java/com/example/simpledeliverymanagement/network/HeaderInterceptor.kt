package com.example.simpledeliverymanagement.network

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("ngrok-skip-browser-warning", "true")
            .build()
        return chain.proceed(request)
    }
}