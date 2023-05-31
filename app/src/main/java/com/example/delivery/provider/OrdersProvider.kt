package com.example.delivery.provider

import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.Address
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.routes.AddressRoutes
import com.example.delivery.routes.OrdersRoutes
import retrofit2.Call

class OrdersProvider(val token: String) {

    private var ordersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        ordersRoutes = api.getOrdersRouters(token)
    }

    fun getOrdersByStatus(status : String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByStatus(status,token)
    }

    fun getOrdersByClientAndStatus(id_client : String,status : String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByClientAndStatus(id_client,status,token)
    }

    fun getOrdersByDeliveryAndStatus(idDelivery: String, status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByDeliveryAndStatus(idDelivery, status, token)
    }

    fun create(order : Order): Call<ResponseHttp>? {
        return ordersRoutes?.create(order,token)

    }

    fun updateToDispatched(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateToDispatched(order, token)
    }


    fun updateToOnTheWay(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateToOnTheWay(order, token)
    }
   fun updateToDelivered(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateToDelivered(order, token)
    }
    fun updateLatLng(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateLatLng(order, token)
    }

}