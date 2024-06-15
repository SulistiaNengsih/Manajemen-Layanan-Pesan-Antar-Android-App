package com.example.simpledeliverymanagement.network.responses

import com.google.gson.annotations.SerializedName
import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class AddFcmResponse(
    @field:SerializedName("data")
    val data: String? = null,

    @field:SerializedName("info")
    val info: String? = null
) : Parcelable