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
import com.example.delivery.models.Address
import com.example.delivery.models.Order
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson


class OrdersClientAdapter(val context: Activity, private val orders: ArrayList<Order>) :
    RecyclerView.Adapter<OrdersClientAdapter.OrdersViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder{

        val view = LayoutInflater.from(context).inflate(R.layout.cardview_orders, parent, false)

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

        holder.itemView.setOnClickListener {goToOrderDetail(order)}


    }

    private fun goToOrderDetail(order : Order){
        val i = Intent(context,ClientOrdersDetailActivity::class.java)
        i.putExtra("order",order.toJson())
        context.startActivity(i)
    }

    class OrdersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textviewOrderId: TextView
        val textviewDate: TextView
        val textviewAddress: TextView

        init {

            textviewOrderId = view.findViewById(R.id.textview_order_id)
            textviewDate = view.findViewById(R.id.textview_date)
            textviewAddress = view.findViewById(R.id.textview_address)

        }
    }


}