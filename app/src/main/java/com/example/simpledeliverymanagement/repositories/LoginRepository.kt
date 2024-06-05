package com.example.simpledeliverymanagement.repositories

import com.example.simpledeliverymanagement.network.ApiService
import com.example.simpledeliverymanagement.network.requests.LoginRequest
import com.example.simpledeliverymanagement.network.responses.LoginResponse
import com.example.simpledeliverymanagement.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {
    private val apiService: ApiService = RetrofitInstance.apiService

    fun login(username: String, password: String, callback: (LoginResponse?) -> Unit) {
        var loginRequest = LoginRequest()
        loginRequest.username = username
        loginRequest.password = password
        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}