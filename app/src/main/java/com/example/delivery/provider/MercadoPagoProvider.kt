package com.example.delivery.provider

import com.example.delivery.api.MercadoPagoApiRoutes
import com.example.delivery.models.MercadoPagoCardTokenBody
import com.example.delivery.routes.MercadoPagoRoutes
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call

class MercadoPagoProvider {

    var mercadoPagoRoutes : MercadoPagoRoutes? = null

    init {
        val api = MercadoPagoApiRoutes()
        mercadoPagoRoutes = api.getMercadoPagoRouters()
    }


    fun getInstallments(bin : String,amount : String): Call<JsonArray>? {
        return mercadoPagoRoutes?.getInstallments(bin, amount)
    }
    fun createCardToken(mercadoPagoCardTokenBody: MercadoPagoCardTokenBody): Call<JsonObject>? {
        return mercadoPagoRoutes?.createCardToken(mercadoPagoCardTokenBody)
    }
}