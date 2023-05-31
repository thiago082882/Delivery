package com.example.delivery.api

import com.example.delivery.routes.AddressRoutes
import com.example.delivery.routes.CategoriasRoutes
import com.example.delivery.routes.MercadoPagoRoutes
import com.example.delivery.routes.OrdersRoutes
import com.example.delivery.routes.ProductsRoutes
import com.example.delivery.routes.UsersRoutes

class MercadoPagoApiRoutes {


    val API_URL = "https://api.mercadopago.com/"
    val retrofit = RetrofitClient()

    fun getMercadoPagoRouters(): MercadoPagoRoutes{
        return retrofit.getClient(API_URL).create(MercadoPagoRoutes::class.java)
    }


}