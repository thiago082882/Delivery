package com.example.delivery.activities.delivery.orders.map

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.SocketEmit
import com.example.delivery.models.User
import com.example.delivery.provider.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.example.delivery.utils.SocketHandler
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.maps.route.extensions.drawRouteOnMap
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeliveryOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback {

    val TAG = "DeliveryOrdersMap"
    var googleMap: GoogleMap? = null


    val PERMISSION_ID = 42
    var fusedLocationClient: FusedLocationProviderClient? = null



    var city = ""
    var country = ""
    var address = ""
    var addressLatLng: LatLng? = null

    var markerDelivery: Marker? = null
    var markerAddress: Marker? = null

    var myLocationLatLng: LatLng? = null

    var order: Order? = null
    var gson = Gson()

    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewNeighborhood: TextView? = null
    var buttonDelivered: Button? = null
    var circleImageUser: CircleImageView? = null
    var imageViewPhone: ImageView? = null

    val REQUEST_PHONE_CALL = 30

    var ordersProvider: OrdersProvider? = null

    var user: User? = null
    var sharedPref: SharedPref? = null

    var distanceBetween = 0.0f

    var socket: Socket? = null


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //Obtem a localizacao  em tempo real
            var lastLocation = locationResult.lastLocation
            if (lastLocation != null) {
                myLocationLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                emitPsition()

//                googleMap?.moveCamera(
//                    CameraUpdateFactory.newCameraPosition(
//                        CameraPosition.builder().target(
//                            LatLng(myLocationLatLng?.latitude!!, myLocationLatLng?.longitude!!)
//                        ).zoom(15f).build()
//                    )
//                )
                distanceBetween = getDistanceBetween(myLocationLatLng!!, addressLatLng!!)
                Log.d(TAG, "Distancia: $distanceBetween")

                removeDeliveryMarker()
                addDeliveryMarker()
                Log.d("LOCALIZAÇÃO", "Callback: $lastLocation")
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_orders_map)

        sharedPref = SharedPref(this)

        getUserFromSession()

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        ordersProvider = OrdersProvider(user?.sessionToken!!)

        addressLatLng = LatLng(order?.address?.lat!!, order?.address?.lng!!)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewNeighborhood = findViewById(R.id.textview_neighborhood)
        circleImageUser = findViewById(R.id.circleimage_user)
        imageViewPhone = findViewById(R.id.imageview_phone)
        buttonDelivered = findViewById(R.id.btn_delivery)

        getLastLocation()

        textViewClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewNeighborhood?.text = order?.address?.neighborhood

        if (!order?.client?.image.isNullOrBlank()) {
            Glide.with(this).load(order?.client?.image).into(circleImageUser!!)
        }
        buttonDelivered?.setOnClickListener {

            if (distanceBetween <= 350) {
                updateOrder()

            } else {
                Toast.makeText(this, "Aproxime-se do local de entrega", Toast.LENGTH_SHORT).show()
            }

        }
        imageViewPhone?.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    REQUEST_PHONE_CALL
                )
            } else {
                call()
            }

        }

        connectSocket()


    }

    private fun emitPsition() {
        val data = SocketEmit(
            id_order = order?.id!!,
            lat = myLocationLatLng?.latitude!!,
            lng = myLocationLatLng?.longitude!!
        )

        socket?.emit("position", data.toJson())
    }

    private fun connectSocket() {
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (locationCallback != null && fusedLocationClient != null) {

            fusedLocationClient?.removeLocationUpdates(locationCallback)

        }

        socket?.disconnect()
    }

    private fun updateOrder() {
        ordersProvider?.updateToDelivered(order!!)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {

                    Toast.makeText(
                        this@DeliveryOrdersMapActivity,
                        "${response.body()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (response.body()?.isSuccess == true) {
                        goToHome()
                    }

                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@DeliveryOrdersMapActivity,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun goToHome() {
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun getDistanceBetween(fromLatLng: LatLng, toLatLng: LatLng): Float {
        var distance = 0.0f

        val from = Location("")
        val to = Location("")

        from.latitude = fromLatLng.latitude
        from.longitude = fromLatLng.longitude

        to.latitude = toLatLng.latitude
        to.longitude = toLatLng.longitude

        distance = from.distanceTo(to)

        return distance
    }

    private fun call() {
        val i = Intent(Intent.ACTION_CALL)
        i.data = Uri.parse("tel:${order?.client?.phone}")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permissão negada para fazer a chamada", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(i)
    }

    private fun drawRoute() {
        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        googleMap?.drawRouteOnMap(
            getString(R.string.google_map_api_key),
            source = myLocationLatLng!!,
            destination = addressLocation,
            context = this,
            color = Color.BLACK,
            polygonWidth = 12,
            boundMarkers = false,
            markers = false
        )

    }

    private fun removeDeliveryMarker() {
        markerDelivery?.remove()

    }

    private fun addDeliveryMarker() {
        markerDelivery = googleMap?.addMarker(
            MarkerOptions()
                .position(myLocationLatLng!!)
                .title("Mi posicion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery))
        )
    }

    private fun addAddressMarker() {

        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)
        markerAddress = googleMap?.addMarker(
            MarkerOptions()
                .position(addressLocation)
                .title("Entregar Aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
        )
    }



    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        try {

            val success = googleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.mapstyle
                )
            )

            if (!success) {
                Log.e(ContentValues.TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(ContentValues.TAG, "Can't find style. Error: ", e)
        }


    }

    private fun updateLatLng(lat: Double, lng: Double) {
        order?.lat = lat
        order?.lng = lng

        ordersProvider?.updateLatLng(order!!)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {

//                    Toast.makeText(
//                        this@DeliveryOrdersMapActivity,
//                        "${response.body()?.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()

                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@DeliveryOrdersMapActivity,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnable()) {

                requestNewLocationData() //Inicia a posicao em tempo real

                fusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                    // Obtem a localização uma unica vez
                    val location = task.result

                    if (location != null) {
                        myLocationLatLng = LatLng(location.latitude, location.longitude)

                        updateLatLng(location.latitude, location.longitude)


                        removeDeliveryMarker()
                        addDeliveryMarker()
                        addAddressMarker()
                        drawRoute()
                        googleMap?.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder().target(
                                    LatLng(location.latitude, location.longitude)
                                ).zoom(15f).build()
                            )
                        )

                    }

                }
            } else {
                Toast.makeText(this, "Habilita a localização", Toast.LENGTH_LONG).show()
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {

        val locationRequest = LocationRequest.Builder(100)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(100F)
            .build()

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
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

    }

    private fun isLocationEnable(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
        if (requestCode == REQUEST_PHONE_CALL) {
            call()
        }
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }

}