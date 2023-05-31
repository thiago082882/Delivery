package com.example.delivery.activities.client.address.list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.client.address.create.ClientAddressCreateActivity
import com.example.delivery.activities.client.payments.mercadopago.forms.ClientPaymentsFormsActivity
import com.example.delivery.activities.client.payments.payment_method.ClientPaymentMethodActivity
import com.example.delivery.adapters.AddressAdapter
import com.example.delivery.models.Address
import com.example.delivery.models.Order
import com.example.delivery.models.Product
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.AddressProvider
import com.example.delivery.provider.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class ClientAddressListActivity : AppCompatActivity() {
    private var fabCreateAddress: FloatingActionButton? = null
    var toolbar: Toolbar? = null
    var btnNext: Button? = null
    var recyclerViewAddress: RecyclerView? = null
    var adapter: AddressAdapter? = null
    var addressProvider: AddressProvider? = null
    var ordersProvider: OrdersProvider? = null
    var sharedPref: SharedPref? = null
    var user: User? = null
    val gson = Gson()

    var address = ArrayList<Address>()
    var selectProducts = ArrayList<Product>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_address_list)


        sharedPref = SharedPref(this)

        getProductsFromSharedPref()

        fabCreateAddress = findViewById(R.id.fab_address_create)
        toolbar = findViewById(R.id.toolbar)
        btnNext = findViewById(R.id.btn_next)
        recyclerViewAddress = findViewById(R.id.recyclerview_address)
        recyclerViewAddress?.layoutManager = LinearLayoutManager(this)

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Meus Endereços"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        getUserFromSession()

        addressProvider = AddressProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        fabCreateAddress?.setOnClickListener { goToAddressCreate() }

        getAddress()

        btnNext?.setOnClickListener { getAddressFromSession() }

    }

    private fun getProductsFromSharedPref() {
        if (!sharedPref?.getData("order").isNullOrBlank()) {
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectProducts = gson.fromJson(sharedPref?.getData("order"), type)


        }
    }

    private fun createOrder(idAddress: String) {

        goToPaymentForms()
//        val order = Order(
//
//            products = selectProducts,
//            idClient = user?.id!!,
//            idAddress = idAddress
//        )
//
//        ordersProvider?.create(order)?.enqueue(object  : Callback<ResponseHttp>{
//            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
//               if(response.body() != null){
//                   Toast.makeText(this@ClientAddressListActivity, "${response.body()?.message}", Toast.LENGTH_LONG).show()
//               }
//                else{
//                   Toast.makeText(this@ClientAddressListActivity, "Ocorreu um erro", Toast.LENGTH_LONG).show()
//               }
//
//            }
//
//            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
//                Toast.makeText(this@ClientAddressListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
//            }
//
//        })
    }


    private fun getAddressFromSession() {

        if (!sharedPref?.getData("address").isNullOrBlank()) {
            val a = gson.fromJson(
                sharedPref?.getData("address"),
                Address::class.java
            )//Se existe um endereco

            createOrder(a.id!!)

            // goToPaymentForms()

        } else {
            Toast.makeText(this, "Selecione um endereço", Toast.LENGTH_LONG).show()
        }
    }

    private fun goToPaymentForms() {
        val i = Intent(this, ClientPaymentMethodActivity::class.java)
        startActivity(i)
    }

    fun resetValue(position: Int) {
        val viewHolder = recyclerViewAddress?.findViewHolderForAdapterPosition(position)
        val view = viewHolder?.itemView
        val imageViewCheck = view?.findViewById<ImageView>(R.id.ivCheck)
        imageViewCheck?.visibility = View.GONE
    }

    private fun getAddress() {
        addressProvider?.getAddress(user?.id!!)?.enqueue(object : Callback<ArrayList<Address>> {
            override fun onResponse(
                call: Call<ArrayList<Address>>,
                response: Response<ArrayList<Address>>
            ) {
                if (response.body() != null) {

                    address = response.body()!!
                    adapter = AddressAdapter(this@ClientAddressListActivity, address)
                    recyclerViewAddress?.adapter = adapter

                }
            }

            override fun onFailure(call: Call<ArrayList<Address>>, t: Throwable) {
                Toast.makeText(
                    this@ClientAddressListActivity,
                    "Error : ${t.message}",
                    Toast.LENGTH_LONG
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

    private fun goToAddressCreate() {
        val i = Intent(this, ClientAddressCreateActivity::class.java)
        startActivity(i)
    }
}