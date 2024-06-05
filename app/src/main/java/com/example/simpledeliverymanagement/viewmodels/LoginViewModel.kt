package com.example.simpledeliverymanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simpledeliverymanagement.network.responses.LoginResponse
import com.example.simpledeliverymanagement.repositories.LoginRepository

class LoginViewModel : ViewModel() {
    private val loginRepository = LoginRepository()
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    fun login(username: String, password: String) {
        loginRepository.login(username, password) { response ->
            _loginResult.postValue(response)
        }
    }
}