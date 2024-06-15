package com.example.simpledeliverymanagement.network.requests

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class UpdateCourierLocationRequest (
    @field:SerializedName("latitude")
    var latitude: String? = null,
    @field:SerializedName("longitude")
        var longitude: String? = null
) : Parcelable