package com.example.simpledeliverymanagement.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpledeliverymanagement.BuildConfig
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.adapters.OrderItemAdapter
import com.example.simpledeliverymanagement.network.RetrofitInstance
import com.example.simpledeliverymanagement.network.responses.DataItem
import com.example.simpledeliverymanagement.network.responses.DirectionsResponse
import com.example.simpledeliverymanagement.viewmodels.OrderViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class ProcessOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val locationPermissionRequestCode = 1
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var origin: String = ""
    private var countRequestMade = 0
    private val orderViewModel: OrderViewModel by viewModels()
    private var getOrderByIdResult: DataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_order)

        // find view
        val namaPemesan: TextView = findViewById(R.id.tvNamaPemesan)
        val noTelepon: TextView = findViewById(R.id.tvNoTelepon)
        val alamat: TextView = findViewById(R.id.tvAlamat)
        val patokan: TextView = findViewById(R.id.tvPatokan)
        val nominal: TextView = findViewById(R.id.tvNominal)
        val pengirim: TextView = findViewById(R.id.tvPengirim)
        val openMapBtn: FloatingActionButton = findViewById(R.id.openMapButton)
        val refreshRouteBtn: FloatingActionButton = findViewById(R.id.reloadPosition)
        val waBtn: ImageView = findViewById(R.id.whatsappIcon)

        // get order detail intent
        val orderId = intent.getIntExtra("orderId", -1)

        // call api get order by id
        orderViewModel.getOrderById(orderId)

        // observe api get order by id respose
        orderViewModel.getOrderByIdResult.observe(this) { result ->
            if (result?.data == null) {
                Toast.makeText(this, "Terjadi kegagalan dalam mendapatkan detail pesanan.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ToBeDeliveredOrderActivity::class.java)
                startActivity(intent)
                finish()
            }

            if (result?.info != "Ok") {
                Toast.makeText(this, result?.info, Toast.LENGTH_SHORT).show()
            }

            if (result?.data?.sendWhatsappUrl != null) {
                sendWhatsapp(this, "", "", result?.data?.sendWhatsappUrl)
            }

            getOrderByIdResult = result?.data

            // set order number
            val orderNumber: String = getOrderByIdResult?.orderNumber?: ""
            supportActionBar?.title = "Pesanan #$orderNumber"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // set recycler view
            val rvOrderItem: RecyclerView = findViewById(R.id.rvOrderItems)
            rvOrderItem.layoutManager = LinearLayoutManager(this)
            val orderItemAdapter = OrderItemAdapter(getOrderByIdResult?.orderItems)
            rvOrderItem.adapter = orderItemAdapter

            // set order detail
            namaPemesan.text = getOrderByIdResult?.custName
            noTelepon.text = getOrderByIdResult?.custPhoneNumber
            alamat.text = getOrderByIdResult?.orderDelivery?.deliveryAddress
            patokan.text = getOrderByIdResult?.orderDelivery?.deliveryRemark
            nominal.text = getOrderByIdResult?.cashAmount.toString()
            pengirim.text = getOrderByIdResult?.deliveredBy

            // set open map, refresh route, and send whatsapp button on click listener
            if (getOrderByIdResult?.orderStatus.equals("Diterima") || getOrderByIdResult?.orderStatus.equals("Dibatalkan")) {
                openMapBtn.setOnClickListener {
                    Toast.makeText(this,"Pesanan telah ${getOrderByIdResult?.orderStatus}.", Toast.LENGTH_SHORT).show()
                }
                refreshRouteBtn.setOnClickListener {
                    Toast.makeText(this,"Pesanan telah ${getOrderByIdResult?.orderStatus}.", Toast.LENGTH_SHORT).show()
                }
                waBtn.setOnClickListener {
                    Toast.makeText(this,"Pesanan telah ${getOrderByIdResult?.orderStatus}.", Toast.LENGTH_SHORT).show()
                }
            } else {
                openMapBtn.setOnClickListener {
                    openGoogleMaps(this, getOrderByIdResult?.orderDelivery?.deliverylatlng ?: "")
                }
                refreshRouteBtn.setOnClickListener {
                    updateRoute()
                }
                waBtn.setOnClickListener {
                    sendWhatsapp(this, getOrderByIdResult?.custPhoneNumber?: "", "Selamat pagi/siang/malam Bapak/Ibu ${namaPemesan.text}, saya ${pengirim.text} ... ")
                }
            }

            updateOrderStatus()

            // set up map fragment
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapFragment) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        // manage bottomSheet layout
        val bottomSheet: FrameLayout = findViewById(R.id.orderDetail)
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 200
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun updateOrderStatus() {
        val tvFirstStatus: TextView = findViewById(R.id.tvInProgress)
        val tvSecondStatus: TextView = findViewById(R.id.tvOnDelivery)
        val tvThirdStatus: TextView = findViewById(R.id.tvCompleted)
        val tvFirstStatusTime: TextView = findViewById(R.id.tvInProgressTime)
        val tvSecondStatusTime: TextView = findViewById(R.id.tvOnDeliveryTime)
        val tvThirdStatusTime: TextView = findViewById(R.id.tvCompletedTime)
        val actionBtn: Button = findViewById(R.id.btnSelesaikanPengiriman)
        val ivStatus: ImageView = findViewById(R.id.ivOrderProgress)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        val sharedPreferences = getSharedPreferences("logged_in_user", Context.MODE_PRIVATE)
        val loggedInUserName = sharedPreferences.getString("name", "")

        tvFirstStatusTime.text = getOrderByIdResult?.processedAt?.let {
            outputFormat.format(inputFormat.parse(it))
        } ?: ""

        tvFirstStatus.text = "Dalam Proses"

        when (getOrderByIdResult?.orderStatus) {
            "Dalam Proses" -> {
                ivStatus.setImageResource(R.drawable.dalam_proses)
                tvFirstStatus.setTypeface(tvSecondStatus.typeface, Typeface.BOLD)

                actionBtn.text = "Kirim"
                actionBtn.setOnClickListener() {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Kirim pesanan")
                    builder.setMessage("Apakah anda yakin ingin memulai proses pengiriman pesanan ini?")

                    builder.setPositiveButton("Kirim") { dialog, which ->
                        orderViewModel.deliverOrder(getOrderByIdResult?.id?: -1, loggedInUserName?: "")
                    }
                    builder.setNegativeButton("Batalkan", null) // Do nothing on cancel

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
            "Dalam Pengiriman" -> {
                ivStatus.setImageResource(R.drawable.dalam_pengiriman)
                tvSecondStatusTime.text = getOrderByIdResult?.deliveredAt?.let {
                    outputFormat.format(inputFormat.parse(it))
                } ?: ""
                tvSecondStatus.text = "Dalam Pengiriman"
                tvSecondStatus.setTypeface(tvSecondStatus.typeface, Typeface.BOLD)
                tvFirstStatus.setTypeface(tvFirstStatus.typeface, Typeface.NORMAL)

                actionBtn.text = "Selesaikan Pengiriman"
                actionBtn.setOnClickListener() {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Selesikan pesanan")
                    builder.setMessage("Apakah anda yakin ingin menyelesaikan pesanan ini?")

                    builder.setPositiveButton("Selesaikan") { dialog, which ->
                        orderViewModel.completeOrder(getOrderByIdResult?.id?: -1)
                    }
                    builder.setNegativeButton("Batalkan", null) // Do nothing on cancel

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
            "Tertunda" -> {
                ivStatus.setImageResource(R.drawable.dalam_pengiriman)
                tvSecondStatusTime.text = getOrderByIdResult?.suspendedAt?.let {
                    outputFormat.format(inputFormat.parse(it))
                } ?: ""
                tvSecondStatus.text = "Tertunda"
                tvSecondStatus.setTypeface(tvSecondStatus.typeface, Typeface.BOLD)
                tvFirstStatus.setTypeface(tvFirstStatus.typeface, Typeface.NORMAL)
                actionBtn.text = "Lanjutkan Pengiriman"
            }
            "Dibatalkan" -> {
                ivStatus.setImageResource(R.drawable.dibatalkan)

                if (getOrderByIdResult?.deliveredAt != null) {
                    tvSecondStatusTime.text = getOrderByIdResult?.deliveredAt?.let {
                        outputFormat.format(inputFormat.parse(it))
                    } ?: ""
                    tvSecondStatus.text = "Dalam Pengiriman"
                }

                tvThirdStatusTime.text = getOrderByIdResult?.canceledAt?.let {
                    outputFormat.format(inputFormat.parse(it))
                } ?: ""
                tvThirdStatus.text = "Dibatalkan"
                tvThirdStatus.setTypeface(tvSecondStatus.typeface, Typeface.BOLD)
                tvFirstStatus.setTypeface(tvFirstStatus.typeface, Typeface.NORMAL)
                tvSecondStatus.setTypeface(tvSecondStatus.typeface, Typeface.NORMAL)
                actionBtn.isVisible = false
            }
            "Diterima" -> {

                ivStatus.setImageResource(R.drawable.selesai)

                tvSecondStatusTime.text = getOrderByIdResult?.deliveredAt?.let {
                    outputFormat.format(inputFormat.parse(it))
                } ?: ""
                tvThirdStatusTime.text = getOrderByIdResult?.receivedAt?.let {
                    outputFormat.format(inputFormat.parse(it))
                } ?: ""

                tvSecondStatus.text = "Dalam Pengiriman"
                tvThirdStatus.text = "Diterima"

                tvSecondStatus.setTypeface(tvSecondStatus.typeface, Typeface.NORMAL)
                tvFirstStatus.setTypeface(tvFirstStatus.typeface, Typeface.NORMAL)
                tvThirdStatus.setTypeface(tvThirdStatus.typeface, Typeface.BOLD)
                actionBtn.isVisible = false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    locationPermissionRequestCode
                )
            }
        } else {
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                backToListOrder("Tidak dapat membuka peta tanpa izin pelacakan lokasi.")
            }
        }
    }

    private fun startLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    origin = "$latitude,$longitude"

                    orderViewModel.updateCourierLocation(getOrderByIdResult?.orderDelivery?.id?: 0,
                        latitude.toString(), longitude.toString()
                    )
                    if (countRequestMade == 0) {
                        updateRoute()
                    }
                }
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setInterval(6000000)
        locationRequest.setFastestInterval(6000000)
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fusedLocationClient != null) {
            fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
        }
    }

    private fun updateRoute() {
        val loadingMap: ProgressBar = findViewById(R.id.loadingMap)
        loadingMap.isVisible = true

        val destination = getOrderByIdResult?.orderDelivery?.deliverylatlng?: ""
        val apiKey = BuildConfig.MAPS_API_KEY

        if (destination == "") {
            Toast.makeText(this,"terdapat kesalahan dalam mendapatkan destinasi", Toast.LENGTH_SHORT).show()
        }

        if (!getOrderByIdResult?.orderStatus.equals("Diterima" ) && !getOrderByIdResult?.orderStatus.equals("Dibatalkan" )) {
            RetrofitInstance.mapApiService.getDirections(origin, destination, apiKey)
                .enqueue(object : Callback<DirectionsResponse> {
                    override fun onResponse(
                        call: Call<DirectionsResponse>,
                        response: Response<DirectionsResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.routes?.let { routes ->
                                if (routes.isNotEmpty()) {
                                    loadingMap.isVisible = false
                                    routes[0]?.overviewPolyline?.points?.let { encodedPolyline ->
                                        val polylinePoints = PolyUtil.decode(encodedPolyline)
                                        drawPolyline(polylinePoints)
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                        backToListOrder("Terdapat kegagalan dalam menemukan rute.")
                    }
                })
        }
        else {
            loadingMap.isVisible = false
        }
    }

    private fun drawPolyline(points: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .addAll(points)
            .width(5f)
            .color(ContextCompat.getColor(this, R.color.green))

        map.addPolyline(polylineOptions)

        // convert drawable into bitmap
        val vectorDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_moped_24)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        // add start marker
        val startMarkerOptions = MarkerOptions()
            .position(points.first())
            .title("Start Point")
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)) // Use the Bitmap here
        map.addMarker(startMarkerOptions)

        // add end marker
        val endMarkerOptions = MarkerOptions()
            .position(points.last())
            .title("End Point")
        map.addMarker(endMarkerOptions)

        // zoom to the polyline
        val latLngBounds = LatLngBounds.builder()
            .include(points.first())
            .include(points.last())
            .build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))
        countRequestMade++
    }

    private fun backToListOrder(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ToBeDeliveredOrderActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openGoogleMaps(context: Context, destination: String) {
        val gmIntentUrl = Uri.parse("google.navigation:q=$destination")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmIntentUrl)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendWhatsapp(context: Context, phoneNumber: String, message: String = "", urlRes: String = "") {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_VIEW)

        try {
            var url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${URLEncoder.encode(message, "UTF-8")}"
            if (urlRes.isNotEmpty()) {
                url = urlRes
            }

            intent.data = Uri.parse(url)
            intent.setPackage("com.whatsapp")
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening WhatsApp.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
