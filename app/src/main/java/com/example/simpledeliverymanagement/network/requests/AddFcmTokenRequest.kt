package com.example.simpledeliverymanagement.network.requests

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class AddFcmTokenRequest (
    @field:SerializedName("fcmToken")
    var fcmToken: String? = null
) : Parcelable