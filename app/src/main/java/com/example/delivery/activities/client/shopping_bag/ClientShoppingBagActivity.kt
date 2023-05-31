package com.example.delivery.activities.client.shopping_bag

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.client.address.create.ClientAddressCreateActivity
import com.example.delivery.activities.client.address.list.ClientAddressListActivity
import com.example.delivery.adapters.ShoppingBagAdapter
import com.example.delivery.models.Product
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ClientShoppingBagActivity : AppCompatActivity() {


    var rvShoppingBar: RecyclerView? = null
    var tvTotal: TextView? = null
    var btnNext: Button? = null
    var toolbar: Toolbar? = null

    var adapter: ShoppingBagAdapter? = null
    var sharedPref: SharedPref? = null
    var gson = Gson()
    var selectProducts = ArrayList<Product>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_shopping_bag)

        sharedPref = SharedPref(this)

        rvShoppingBar = findViewById(R.id.rvShoppingBag)
        tvTotal = findViewById(R.id.tvTotal)
        btnNext = findViewById(R.id.btn_next)
        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Lista de Compras"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        rvShoppingBar?.layoutManager = LinearLayoutManager(this)

        getProductsFromSharedPref()

        btnNext?.setOnClickListener {goToAddressList()}

    }
    private fun goToAddressList() {
        val i = Intent(this, ClientAddressListActivity::class.java)
        startActivity(i)
    }

    fun setTotal(total: Double) {
        tvTotal?.text = "R$ ${total}"
    }

    private fun getProductsFromSharedPref() {
        if (!sharedPref?.getData("order").isNullOrBlank()) {
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectProducts = gson.fromJson(sharedPref?.getData("order"), type)

            adapter = ShoppingBagAdapter(this, selectProducts)
            rvShoppingBar?.adapter = adapter

        }
    }
}