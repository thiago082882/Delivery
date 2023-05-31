package com.example.delivery.routes

import com.example.delivery.models.MercadoPagoCardTokenBody
import com.example.delivery.models.MercadoPagoPayment
import com.example.delivery.models.ResponseHttp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PaymentsRoutes {


    @POST("payments/create")
    fun createPayment(
        @Body mercadoPagoPayment: MercadoPagoPayment,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>
}