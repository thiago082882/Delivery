package com.example.delivery.activities.client.orders.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.client.orders.map.ClientOrdersMapActivity
import com.example.delivery.activities.delivery.orders.map.DeliveryOrdersMapActivity
import com.example.delivery.adapters.OrderProductsAdapter
import com.example.delivery.models.Order
import com.google.gson.Gson

class ClientOrdersDetailActivity : AppCompatActivity() {

    lateinit var toolbar : Toolbar

    val TAG ="ClientOrdersDetail"
    var order : Order?= null
    var gson = Gson()

    var tvClient : TextView? = null
    var tvAddress : TextView? = null
    var tvDate : TextView? = null
    var tvTotal : TextView? = null
    var tvStatus : TextView? = null
    var rvProducts : RecyclerView? = null
     var adapter: OrderProductsAdapter? = null
    var btnGoToMap : Button?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_orders_detail)
        
        
        order = gson.fromJson(intent.getStringExtra("order"),Order::class.java)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this,R.color.black))
        toolbar?.title = "Order #${order?.id}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvClient = findViewById(R.id.textview_client)
        tvAddress = findViewById(R.id.textview_address)
        tvDate = findViewById(R.id.textview_date)
        tvTotal = findViewById(R.id.textview_total)
        tvStatus = findViewById(R.id.textview_status)
        btnGoToMap = findViewById(R.id.btn_go_to_map)

        rvProducts = findViewById(R.id.recyclerview_products)
        rvProducts?.layoutManager = LinearLayoutManager(this)
        adapter = OrderProductsAdapter(this, order?.products!!)
        rvProducts?.adapter = adapter

        tvClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        tvAddress?.text = order?.address?.address
        tvDate?.text = "${order?.timestamp}"
        tvStatus?.text = order?.status


        Log.d(TAG, "Order: ${order.toString()} ")

        getTotal()

        if(order?.status == "ENCAMINHADO"){
            btnGoToMap?.visibility = View.VISIBLE
        }

        btnGoToMap?.setOnClickListener {goToMap()}

    }


    private fun goToMap() {
        val i = Intent(this, ClientOrdersMapActivity::class.java)
        i.putExtra("order",order?.toJson())
        startActivity(i)
    }

    private fun getTotal(){
        var total = 0.0
        for(p in order?.products!!){
            total = total + (p.price * p.quantity!!)
        }
        tvTotal?.text = "R$ ${total}"
    }
}