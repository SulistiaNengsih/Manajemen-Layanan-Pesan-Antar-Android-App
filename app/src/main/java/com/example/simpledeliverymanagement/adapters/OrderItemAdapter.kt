package com.example.simpledeliverymanagement.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.activities.ProcessOrderActivity
import com.example.simpledeliverymanagement.network.responses.DataItem
import com.example.simpledeliverymanagement.network.responses.OrderItemsItem

class OrderItemAdapter(private val orders: List<OrderItemsItem?>?) :
    RecyclerView.Adapter<OrderItemAdapter.OrderProductsViewHolder>() {

    inner class OrderProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productAmountAndName: TextView = itemView.findViewById(R.id.productPriceAndName)
        val productTotalPrice: TextView = itemView.findViewById(R.id.productTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_products, parent, false)
        return OrderProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val product = orders?.get(position)
        holder.productAmountAndName.text = product?.product?.productName
        holder.productTotalPrice.text = product?.formattedSubtotal
    }

    override fun getItemCount(): Int = orders!!.size
}
