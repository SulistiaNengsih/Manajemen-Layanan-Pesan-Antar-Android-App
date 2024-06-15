package com.example.simpledeliverymanagement.network.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetOrderResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("info")
	val info: String? = null
) : Parcelable

@Parcelize
data class GetOrderByIdResponse(

	@field:SerializedName("data")
	val data: DataItem? = null,

	@field:SerializedName("info")
	val info: String? = null
) : Parcelable

@Parcelize
data class Product(

	@field:SerializedName("product_desc")
	val productDesc: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("available_qty")
	val availableQty: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("unit_price")
	val unitPrice: Int? = null,

	@field:SerializedName("formatted_unit_price")
	val formattedUnitPrice: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null
) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("delivered_by")
	val deliveredBy: String? = null,

	@field:SerializedName("cash_amount")
	val cashAmount: Int? = null,

	@field:SerializedName("formatted_cash_amount")
	val formattedCashAmount: String? = null,

	@field:SerializedName("cancel_remark")
	val cancelRemark: String? = null,

	@field:SerializedName("canceled_at")
	val canceledAt: String? = null,

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("send_whatsapp_url")
	val sendWhatsappUrl: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("cust_phone_number")
	val custPhoneNumber: String? = null,

	@field:SerializedName("total_payment")
	val totalPayment: Int? = null,

	@field:SerializedName("formatted_total_payment")
	val formattedTotalPayment: String? = null,

	@field:SerializedName("order_status")
	val orderStatus: String? = null,

	@field:SerializedName("suspend_remark")
	val suspendRemark: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("order_delivery")
	val orderDelivery: OrderDelivery? = null,

	@field:SerializedName("received_at")
	val receivedAt: String? = null,

	@field:SerializedName("processed_at")
	val processedAt: String? = null,

	@field:SerializedName("cust_name")
	val custName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("tracking_url")
	val trackingUrl: String? = null,

	@field:SerializedName("delivered_at")
	val deliveredAt: String? = null,

	@field:SerializedName("suspended_at")
	val suspendedAt: String? = null,

	@field:SerializedName("order_items")
	val orderItems: List<OrderItemsItem?>? = null
) : Parcelable

@Parcelize
data class OrderDelivery(

	@field:SerializedName("courier_latitude")
	val courierLatitude: String? = null,

	@field:SerializedName("delivery_address")
	val deliveryAddress: String? = null,

	@field:SerializedName("location_name")
	val locationName: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("courier_longitude")
	val courierLongitude: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("delivery_remark")
	val deliveryRemark: String? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null,

	@field:SerializedName("delivery_latitude")
	val deliveryLatitude: String? = null,

	@field:SerializedName("delivery_longitude")
	val deliveryLongitude: String? = null,

	@field:SerializedName("deliverylatlng")
	val deliverylatlng: String? = null
) : Parcelable

@Parcelize
data class OrderItemsItem(

	@field:SerializedName("product")
	val product: Product? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("subtotal")
	val subtotal: Int? = null,

	@field:SerializedName("formatted_subtotal")
	val formattedSubtotal: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order_qty")
	val orderQty: Int? = null,

	@field:SerializedName("unit_price")
	val unitPrice: Int? = null,

	@field:SerializedName("formatted_unit_price")
	val formattedUnitPrice: String? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null
) : Parcelable