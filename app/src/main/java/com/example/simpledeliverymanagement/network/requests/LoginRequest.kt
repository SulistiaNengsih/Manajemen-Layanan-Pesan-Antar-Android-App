package com.example.simpledeliverymanagement.network.requests

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class LoginRequest(

	@field:SerializedName("password")
	var password: String? = null,

	@field:SerializedName("username")
	var username: String? = null
) : Parcelable
