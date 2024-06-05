package com.example.simpledeliverymanagement.network.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class LoginResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("info")
	val info: String? = null
) : Parcelable

@Parcelize
data class LoggedInUser(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("role")
	val role: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("username")
	val username: String? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("logged_in_user")
	val loggedInUser: LoggedInUser? = null,

	@field:SerializedName("token")
	val token: String? = null
) : Parcelable
