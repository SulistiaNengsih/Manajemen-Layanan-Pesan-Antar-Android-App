package com.example.simpledeliverymanagement.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.adapters.OrderAdapter
import com.example.simpledeliverymanagement.network.responses.DataItem
import com.example.simpledeliverymanagement.viewmodels.OrderViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ToBeDeliveredOrderActivity : AppCompatActivity() {
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var pbLoading: ProgressBar
    private val orderViewModel: OrderViewModel by viewModels()
    private var allOrders: List<DataItem?> = emptyList()
    private enum class FilterType {ALL, TO_BE_SHIPPED, ON_DELIVERY, RECEIVED, SUSPENDED, CANCELED}
    private var current_filter = FilterType.TO_BE_SHIPPED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_be_delivered_order)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

//        bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_to_be_delivered -> {
//                    true
//                }
//                R.id.navigation_history -> {
//                    true
//                }
//                else -> false
//            }
//        }

        pbLoading = findViewById(R.id.pbLoading)
        val ivNoOrder: ImageView = findViewById(R.id.ivNoOrder)
        val tvNoOrder: TextView = findViewById(R.id.tvNoOrder)

        pbLoading.isVisible = true

        orderRecyclerView = findViewById(R.id.rvListToBeDeliveredOrder)
        orderRecyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(mutableListOf()) { clickedOrder ->
            val intent = Intent(this, ProcessOrderActivity::class.java)
                .apply {
                    putExtra("orderId", clickedOrder?.id)
                }
            startActivity(intent)
            finish()
        }
        orderRecyclerView.adapter = orderAdapter

        orderViewModel.orderResult.observe(this) { orderResult ->
            val orderCount = orderResult?.data?.count() ?: 0
            pbLoading.isVisible = false
            if (orderCount > 0) {
                // set image view and text view visibility to false
                ivNoOrder.isVisible = false
                tvNoOrder.isVisible = false

                // assign orders from api to orderResult
                allOrders = orderResult?.data!!
                applyFilter(current_filter)
            } else {
                var statusPesanan = ""
                when (current_filter) {
                    FilterType.ALL -> statusPesanan = ""
                    FilterType.TO_BE_SHIPPED -> statusPesanan = " belum dikirim"
                    FilterType.ON_DELIVERY -> statusPesanan = " dalam pengiriman"
                    FilterType.RECEIVED -> statusPesanan = " diterima"
                    FilterType.SUSPENDED -> statusPesanan = " ditunda"
                    FilterType.CANCELED -> statusPesanan = " dibatalkan"
                }
                ivNoOrder.isVisible = true
                tvNoOrder.isVisible = true
                tvNoOrder.text = "Tidak ada pesanan $statusPesanan"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.to_be_delivered_filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        pbLoading.isVisible = true
        return when (item.itemId) {
            R.id.filter_all -> {
                applyFilter(FilterType.ALL)
                true
            }
            R.id.filter_to_be_shipped -> {
                applyFilter(FilterType.TO_BE_SHIPPED)
                true
            }
            R.id.filter_on_delivery -> {
                applyFilter(FilterType.ON_DELIVERY)
                true
            }
            R.id.filter_received -> {
                applyFilter(FilterType.RECEIVED)
                true
            }
            R.id.filter_delayed -> {
                applyFilter(FilterType.SUSPENDED)
                true
            }
            R.id.filter_canceled -> {
                applyFilter(FilterType.CANCELED)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applyFilter(filterType: FilterType) {
        val filteredOrders = when (filterType) {
            FilterType.ALL -> allOrders.filter {it?.orderStatus != "Temporer"}
            FilterType.TO_BE_SHIPPED -> allOrders.filter { it?.orderStatus == "Dalam Proses" }
            FilterType.ON_DELIVERY -> allOrders.filter { it?.orderStatus == "Dalam Pengiriman" }
            FilterType.RECEIVED -> allOrders.filter { it?.orderStatus == "Diterima" }
            FilterType.SUSPENDED -> allOrders.filter { it?.orderStatus == "Tertunda" }
            FilterType.CANCELED -> allOrders.filter { it?.orderStatus == "Dibatalkan" }
        }
        current_filter = filterType
        orderAdapter.updateOrders(filteredOrders)
    }
}
