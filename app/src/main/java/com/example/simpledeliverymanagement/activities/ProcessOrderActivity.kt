package com.example.simpledeliverymanagement.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpledeliverymanagement.R
import com.example.simpledeliverymanagement.adapters.OrderItemAdapter
import com.example.simpledeliverymanagement.network.responses.DirectionsResponse
import com.example.simpledeliverymanagement.network.RetrofitInstance
import com.example.simpledeliverymanagement.network.responses.DataItem
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProcessOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var origin: String = ""
    private val CHANNEL_ID = "location_channel"
    private var countRequestMade = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_progress_order)

        var bottomSheet: FrameLayout = findViewById(R.id.orderDetail)
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 200
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // TODO: 8. add referesh location button

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val orderDetail = intent.getParcelableExtra<DataItem?>("orderDetail")

        val namaPemesan : TextView = findViewById(R.id.tvNamaPemesan)
        val noTelepon : TextView = findViewById(R.id.tvNoTelepon)
        val alamat : TextView = findViewById(R.id.tvAlamat)
        val patokan : TextView = findViewById(R.id.tvPatokan)
        val nominal : TextView = findViewById(R.id.tvNominal)
        val pengirim : TextView = findViewById(R.id.tvPengirim)

        namaPemesan.text = orderDetail?.custName
        noTelepon.text = orderDetail?.custPhoneNumber
        alamat.text = orderDetail?.orderDelivery?.deliveryAddress
        patokan.text = orderDetail?.orderDelivery?.deliveryRemark
        nominal.text = orderDetail?.cashAmount.toString()
        pengirim.text = orderDetail?.deliveredBy

        val rvOrderItem : RecyclerView = findViewById(R.id.rvOrderItems)
        rvOrderItem.layoutManager = LinearLayoutManager(this)

        val orderItemAdapter = OrderItemAdapter(orderDetail?.orderItems)
        rvOrderItem.adapter = orderItemAdapter
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        requestLocationPermissions();
    }

    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            startLocationService(map)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService(map)
            } else {
                backToListOrder("Tidak dapat membuka peta tanpa izin pelacakan lokasi.")
            }
        }
    }

    private fun startLocationService(map: GoogleMap) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    origin = "$latitude,$longitude"

                    if (countRequestMade == 0) {
                        updateRoute()
                    }
                }
            }
        }

        startLocationUpdates();
    }

    private fun startLocationUpdates() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setInterval(600000)
        locationRequest.setFastestInterval(600000)
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
        fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
    }

    private fun updateRoute() {
        var loadingMap : ProgressBar = findViewById(R.id.loadingMap)
        loadingMap.isVisible = true

        // TODO: 6. obtain destination from intent
        val destination = "-7.952344286202501,112.61365097164908"
        val apiKey = "AIzaSyAk3L-mrPIoizugy1dkD-vHj1q2EjrwENs"

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

    private fun drawPolyline(points: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .addAll(points)
            .width(5f)
            .color(ContextCompat.getColor(this, R.color.yellow))

        map.addPolyline(polylineOptions)

        // add marker
        val startMarkerOptions = MarkerOptions()
            .position(points.first())
            .title("Start Point")
        map.addMarker(startMarkerOptions)

        val endMarkerOptions = MarkerOptions()
            .position(points.last())
            .title("End Point")
        map.addMarker(endMarkerOptions)

        // Zoom to the polyline
        val latLngBounds = LatLngBounds.builder()
            .include(points.first())
            .include(points.last())
            .build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))
        countRequestMade++
    }

    private fun backToListOrder(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ListOrderActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openGoogleMaps() {
        // TODO: 7. add open to GoogleMaps
    }
}
