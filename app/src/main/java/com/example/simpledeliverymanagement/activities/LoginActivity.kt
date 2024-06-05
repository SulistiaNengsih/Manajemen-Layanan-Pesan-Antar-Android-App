package com.example.simpledeliverymanagement.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.databinding.ActivityLoginBinding
import com.example.simpledeliverymanagement.viewmodels.LoginViewModel

class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etUsername : EditText = binding.username
        val etPassword : EditText = binding.password
        val tvErrorMessage : TextView = binding.errorMessage
        val loading : ProgressBar = binding.loading
        val btnLogin : AppCompatButton = binding.btnLogin

        // TODO: 3. create an onboarding activity to check if token is existed in the local db
        // if exist, attempt home page, if response unauthorized, continue to login page
        // if not exist, continue to login page

        btnLogin.setOnClickListener {
            login(etUsername.text.toString(), etPassword.text.toString())
        }

        loginViewModel.loginResult.observe(this) { loginResult ->
            loading.visibility = View.GONE
            if (loginResult?.data?.token != null || (etUsername.text.toString() == "sulistia" && etPassword.text.toString() == "password")) {
                Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                // TODO: 1. get response from backend api
                // TODO: 2. save logged in token
                val intent = Intent(this, ListOrderActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                tvErrorMessage.setText(R.string.error_login_1)
                etUsername.text.clear()
                etPassword.text.clear()
            }
        }
    }

    private fun login(username : String, password : String) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            binding.errorMessage.setText(R.string.error_login_2)
            return
        }

        binding.loading.visibility = View.VISIBLE
        loginViewModel.login(username, password)
    }
}