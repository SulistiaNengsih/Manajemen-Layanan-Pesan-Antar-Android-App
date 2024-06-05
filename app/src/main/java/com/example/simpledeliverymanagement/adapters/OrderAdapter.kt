package com.example.simpledeliverymanagement.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.activities.ProcessOrderActivity
import com.example.simpledeliverymanagement.network.responses.DataItem

class OrderAdapter(private var orders: MutableList<DataItem?>?) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderNumberTextView: TextView = itemView.findViewById(R.id.orderNumberTextView)
        val customerNameTextView: TextView = itemView.findViewById(R.id.customerNameTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val sendButton: Button = itemView.findViewById(R.id.sendButton)
        val orderProductsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.orderItemsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders?.get(position)
        holder.orderNumberTextView.text = "Pesanan #${currentOrder?.orderNumber}"
        holder.customerNameTextView.text = currentOrder?.custName
        holder.addressTextView.text = currentOrder?.orderDelivery?.deliveryAddress
        holder.phoneNumberTextView.text = currentOrder?.custPhoneNumber
        holder.sendButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProcessOrderActivity::class.java).apply {
                putExtra("orderDetail", currentOrder)
            }
            context.startActivity(intent)
        }
        holder.orderProductsRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context)
        val orderItemAdapter = OrderItemAdapter(currentOrder?.orderItems)
        holder.orderProductsRecyclerView.adapter = orderItemAdapter
    }

    override fun getItemCount(): Int = orders!!.size

    fun updateOrders(newOrders: List<DataItem?>) {
        orders?.clear()
        orders?.addAll(newOrders)
        notifyDataSetChanged()
    }
}
