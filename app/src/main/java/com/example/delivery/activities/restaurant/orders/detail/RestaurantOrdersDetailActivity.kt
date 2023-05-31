package com.example.delivery.activities.restaurant.orders.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.adapters.OrderProductsAdapter
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.OrdersProvider
import com.example.delivery.provider.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class RestaurantOrdersDetailActivity : AppCompatActivity() {


    val TAG = "RestaurantOrdersDetail"
    lateinit var toolbar: Toolbar
    var order: Order? = null
    var gson = Gson()
    var tvClient: TextView? = null
    var tvAddress: TextView? = null
    var tvDate: TextView? = null
    var tvTotal: TextView? = null
    var tvStatus: TextView? = null
    var tvDelivery: TextView? = null
    var tvDeliveryAssigned: TextView? = null
    var tvDeliveryAvailable: TextView? = null
    var btnUpdate: Button? = null

    var rvProducts: RecyclerView? = null
    var adapter: OrderProductsAdapter? = null

    var usersProvider: UsersProvider? = null
    var ordersProvider: OrdersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null

    var spinnerDeliveryMen: Spinner? = null
    var idDelivery = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_orders_detail)

        sharedPref = SharedPref(this)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        getUserFromSession()

        usersProvider = UsersProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Order #${order?.id}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvClient = findViewById(R.id.textview_client)
        tvAddress = findViewById(R.id.textview_address)
        tvDate = findViewById(R.id.textview_date)
        tvTotal = findViewById(R.id.textview_total)
        tvStatus = findViewById(R.id.textview_status)
        tvDelivery = findViewById(R.id.textview_delivery)
        tvDeliveryAssigned = findViewById(R.id.tvDeliveryAssigned)
       tvDeliveryAvailable = findViewById(R.id.tvDeliveryAvailable)
        spinnerDeliveryMen = findViewById(R.id.spinner_delivery_men)
        btnUpdate = findViewById(R.id.btn_update)


        rvProducts = findViewById(R.id.recyclerview_products)
        rvProducts?.layoutManager = LinearLayoutManager(this)


        adapter = OrderProductsAdapter(this, order?.products!!)
        rvProducts?.adapter = adapter

        tvClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        tvAddress?.text = order?.address?.address
        tvDate?.text = "${order?.timestamp}"
        tvStatus?.text = order?.status
        tvDelivery?.text ="${order?.delivery?.name} ${order?.delivery?.lastname}"


        Log.d(TAG, "Order: ${order.toString()} ")
        getTotal()

        getDeliveryMen()


        if (order?.status == "PAGO") {
            btnUpdate?.visibility = View.VISIBLE
            tvDeliveryAvailable?.visibility = View.VISIBLE
            spinnerDeliveryMen?.visibility = View.VISIBLE
        }

        if (order?.status != "PAGO") {
            tvDeliveryAssigned?.visibility = View.VISIBLE
            tvDelivery?.visibility = View.VISIBLE
        }

        btnUpdate?.setOnClickListener { updateOrder() }
    }

    private fun updateOrder() {
        order?.idDelivery = idDelivery

        ordersProvider?.updateToDispatched(order!!)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                if (response.body() != null) {


                    if (response.body()?.isSuccess == true) {
                        Toast.makeText(
                            this@RestaurantOrdersDetailActivity,
                            "Entregador atribuído corretamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        goToOrders()
                    } else {
                        Toast.makeText(
                            this@RestaurantOrdersDetailActivity,
                            "Não foi possível atribuir o entregador",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RestaurantOrdersDetailActivity,
                        "Não houve resposta do servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@RestaurantOrdersDetailActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun goToOrders() {
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun getDeliveryMen() {
        usersProvider?.getDeliveryMen()?.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {

                if (response.body() != null) {
                    val deliveryMen = response.body()

                    val arrayAdapter = ArrayAdapter<User>(
                        this@RestaurantOrdersDetailActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        deliveryMen!!
                    )
                    spinnerDeliveryMen?.adapter = arrayAdapter
                    spinnerDeliveryMen?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                l: Long
                            ) {
                                idDelivery =
                                    deliveryMen[position].id!! // SELECIONANDO A ID DE ENTREGA DO SPINNER
                                Log.d(TAG, "Id Delivery: $idDelivery")
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                }

            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                Toast.makeText(
                    this@RestaurantOrdersDetailActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }

//    private fun getTotal() {
//        var total = 0.0
//        for (p in order?.products!!) {
//            total = total + (p.price * p.quantity!!)
//        }
//        tvTotal?.text = "R$ ${total}"
//    }

    private fun getTotal() {
        var total = 0.0
        for (p in order?.products!!) {
            total += p.price * p.quantity!!
        }
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        tvTotal?.text = numberFormat.format(total)
    }

}