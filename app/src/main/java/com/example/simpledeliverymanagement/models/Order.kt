package com.example.simpledeliverymanagement.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderNumber: Int,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val customerName: String,
    val address: String,
    val phoneNumber: String
) : Parcelable

@Parcelize
data class Product(
    val quantity: Int,
    val productName: String,
    val productPrice: Double
): Parcelable