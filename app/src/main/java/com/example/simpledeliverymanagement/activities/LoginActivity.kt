package com.example.simpledeliverymanagement.activities

import android.content.Context
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.databinding.ActivityLoginBinding
import com.example.simpledeliverymanagement.viewmodels.LoginViewModel
import com.example.simpledeliverymanagement.viewmodels.OrderViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        FirebaseApp.initializeApp(this)

        // check if logged in user exist and existing fcm token
        val sharedPreferences = getSharedPreferences("logged_in_user", Context.MODE_PRIVATE)
        var fcmToken = sharedPreferences.getString("fcm_token", null)
        val token = sharedPreferences.getString("token", null)
        val role = sharedPreferences.getInt("role", -1)

        if (fcmToken == null || fcmToken.isEmpty()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fcmToken = task.result
                    // Store the token in SharedPreferences
                    sharedPreferences.edit().putString("fcm_token", token).apply()
                    orderViewModel.addFcmToken(fcmToken?: "")
                } else {
                    // Handle errors
                }
            }
        } else {
            // Token already exists, you can use it or send it to the backend again if needed
        }

        // if exist, open homepage according to logged in user role
        if (token != null && role >= 0) {
            openHomepage(role)
        }

        // if not, bind view
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etUsername: EditText = binding.etUsername
        val etPassword: EditText = binding.etPassword
        val tvErrorMessage: TextView = binding.tvErrorMessage
        val loading: ProgressBar = binding.pbLoading
        val btnLogin: AppCompatButton = binding.btnLogin

        // set button login on click listener
        btnLogin.setOnClickListener {
            login(etUsername.text.toString(), etPassword.text.toString())
        }

        // observe login result
        loginViewModel.loginResult.observe(this) { loginResult ->
            loading.visibility = View.GONE
            if (loginResult?.data?.token != null && loginResult?.data?.loggedInUser != null) {

                // save logged in data to shared preferences
                with(sharedPreferences.edit()) {
                    putInt("user_id", loginResult.data?.loggedInUser?.id ?: 0)
                    putInt("role", loginResult.data?.loggedInUser?.role ?: -1)
                    putString("token", loginResult.data?.token)
                    putString("username", loginResult.data?.loggedInUser.username)
                    putString("name", loginResult.data?.loggedInUser.name)
                    apply()
                }

                // open homepage according to logged in user role
                openHomepage(loginResult?.data?.loggedInUser?.role ?: -1)
            } else {
                // shows error if no login result
                tvErrorMessage.setText(R.string.error_login_1)
                etUsername.text.clear()
                etPassword.text.clear()
            }
        }
    }

    private fun login(username: String, password: String) {
        try {
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                binding.tvErrorMessage.setText(R.string.error_login_2)
                return
            }

            binding.pbLoading.visibility = View.VISIBLE
            loginViewModel.login(username, password)
        }
        catch (e: Exception) {
            binding.tvErrorMessage.setText("Terjadi kesalahan dalam memverifikasi data login")
            return
        }
    }

    private fun openHomepage(role: Int) {
        if (role == 0) {
            // TODO: create admin page
            // TODO: use jwt barrier authentication
            Toast.makeText(this, "User adalah admin", Toast.LENGTH_SHORT).show()
        }
        if (role == 1) {
            val intent = Intent(this, ToBeDeliveredOrderActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}