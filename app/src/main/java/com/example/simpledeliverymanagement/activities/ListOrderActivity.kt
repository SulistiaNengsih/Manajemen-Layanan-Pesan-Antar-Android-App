package com.example.simpledeliverymanagement.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.adapters.OrderAdapter
import com.example.simpledeliverymanagement.viewmodels.OrderViewModel

class ListOrderActivity : AppCompatActivity() {
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_list_order)

        // TODO: 5. create bottom navigation

        orderRecyclerView = findViewById(R.id.orderRecyclerView)
        orderRecyclerView.layoutManager = LinearLayoutManager(this)

        orderAdapter = OrderAdapter(mutableListOf())
        orderRecyclerView.adapter = orderAdapter

        orderViewModel.orderResult.observe(this) { orderResult ->
            if (orderResult?.data?.count()!! > 0) {
                orderAdapter.updateOrders(orderResult?.data)
            } else {

            }
        }
    }
}
