package com.example.delivery.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.client.address.list.ClientAddressListActivity
import com.example.delivery.activities.client.orders.detail.ClientOrdersDetailActivity
import com.example.delivery.activities.delivery.orders.detail.DeliveryOrdersDetailActivity
import com.example.delivery.activities.restaurant.orders.detail.RestaurantOrdersDetailActivity
import com.example.delivery.models.Address
import com.example.delivery.models.Order
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson


class OrdersDeliveryAdapter(val context: Activity, private val orders: ArrayList<Order>) :
    RecyclerView.Adapter<OrdersDeliveryAdapter.OrdersViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder{

        val view = LayoutInflater.from(context).inflate(R.layout.cardview_orders_restaurant, parent, false)

        return OrdersViewHolder(view)

    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {

        val order = orders[position]


        holder.textviewOrderId.text = "Ordem #${order.id}"
        holder.textviewDate.text = "${order.timestamp}"
        holder.textviewAddress.text = "${order.address?.address}"
        holder.textviewClient.text = "${order.client?.name} ${order.client?.lastname}"

        holder.itemView.setOnClickListener {goToOrderDetail(order)}


    }

    private fun goToOrderDetail(order : Order){
        val i = Intent(context, DeliveryOrdersDetailActivity::class.java)
        i.putExtra("order",order.toJson())
        context.startActivity(i)
    }

    class OrdersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textviewOrderId: TextView
        val textviewDate: TextView
        val textviewAddress: TextView
        val textviewClient: TextView

        init {

            textviewOrderId = view.findViewById(R.id.textview_order_id)
            textviewDate = view.findViewById(R.id.textview_date)
            textviewAddress = view.findViewById(R.id.textview_address)
            textviewClient= view.findViewById(R.id.textview_client)

        }
    }


}