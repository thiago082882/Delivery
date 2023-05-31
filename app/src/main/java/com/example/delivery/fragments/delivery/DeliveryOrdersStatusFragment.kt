package com.example.delivery.fragments.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.adapters.OrdersDeliveryAdapter
import com.example.delivery.adapters.OrdersRestaurantAdapter
import com.example.delivery.models.Order
import com.example.delivery.models.User
import com.example.delivery.provider.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeliveryOrdersStatusFragment : Fragment() {

    var myView: View? = null
    var ordersProvider: OrdersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var recyclerViewOrders: RecyclerView? = null
    var adapter: OrdersDeliveryAdapter? = null
    var status = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView= inflater.inflate(R.layout.fragment_delivery_orders_status, container, false)
        sharedPref = SharedPref(requireActivity())

        status = arguments?.getString("status")!!
        //status = arguments?.getString("status") ?: "defaultStatus"
        //arguments?.getString("status")?.let { status = it }


        getUserFromSession()
        ordersProvider = OrdersProvider(user?.sessionToken!!)
        recyclerViewOrders = myView?.findViewById(R.id.recyclerview_orders)
        recyclerViewOrders?.layoutManager = LinearLayoutManager(requireContext())

        getOrders()

        return myView

    }

    private fun getOrders() {
        ordersProvider?.getOrdersByDeliveryAndStatus(user?.id!!,status)?.enqueue(object :
            Callback<ArrayList<Order>> {
            override fun onResponse(
                call: Call<ArrayList<Order>>,
                response: Response<ArrayList<Order>>
            ) {

                if (response.body() != null) {
                    val orders = response.body()
                    adapter = OrdersDeliveryAdapter(requireActivity(), orders!!)
                    recyclerViewOrders?.adapter = adapter
                }


            }

            override fun onFailure(call: Call<ArrayList<Order>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error:  ${t.message}", Toast.LENGTH_LONG).show()
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


}