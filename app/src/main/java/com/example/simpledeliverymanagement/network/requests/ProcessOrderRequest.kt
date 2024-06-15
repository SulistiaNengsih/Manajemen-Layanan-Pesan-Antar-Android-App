package com.example.simpledeliverymanagement.network.requests

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ProcessOrderRequest(
    @field:SerializedName("delivered_by")
    var deliveredBy: String? = null,
) : Parcelable
