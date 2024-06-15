package com.example.simpledeliverymanagement.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.activities.ProcessOrderActivity
import com.example.simpledeliverymanagement.network.responses.DataItem

class OrderAdapter(private var orders: MutableList<DataItem?>?, private val onItemClickListener: (DataItem?) -> Unit) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderDateTextView: TextView = itemView.findViewById(R.id.tvDate)
        val orderNumberTextView: TextView = itemView.findViewById(R.id.orderNumberTextView)
        val customerNameTextView: TextView = itemView.findViewById(R.id.customerNameTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val sendButton: Button = itemView.findViewById(R.id.seeDetailBtn)
        val orderStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val totalTextView: TextView = itemView.findViewById(R.id.totalTv)
        val orderProductsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.orderItemsRecyclerView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener(orders?.get(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders?.get(position)

        val year = currentOrder?.orderNumber?.substring(0,2)
        val month = currentOrder?.orderNumber?.substring(2, 4)
        val date = currentOrder?.orderNumber?.substring(4,6)

        holder.orderDateTextView.text = "${date}/${month}/${year}"
        holder.orderNumberTextView.text = "Pesanan #${currentOrder?.orderNumber}"
        holder.customerNameTextView.text = currentOrder?.custName
        holder.addressTextView.text = currentOrder?.orderDelivery?.deliveryAddress
        holder.phoneNumberTextView.text = currentOrder?.custPhoneNumber
        holder.orderStatus.text = currentOrder?.orderStatus
        holder.totalTextView.text = "Total pembayaran: ${currentOrder?.formattedTotalPayment}"

        if (currentOrder?.orderStatus == "Dalam Proses") {
            holder.orderStatus.text = "Belum Dikirim"
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_grey))
        }
        else if (currentOrder?.orderStatus == "Dalam Pengiriman") {
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_500))
        }
        else if (currentOrder?.orderStatus == "Diterima") {
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }
        else if (currentOrder?.orderStatus == "Tertunda") {
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.yellow))
        }
        else if (currentOrder?.orderStatus == "Dibatalkan") {
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

        holder.sendButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProcessOrderActivity::class.java).apply {
                putExtra("orderId", currentOrder?.id)
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
