package com.example.delivery.routes

import com.example.delivery.models.MercadoPagoCardTokenBody
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MercadoPagoRoutes {


    @GET("v1/payment_methods/installments?access_token=TEST-5210485235887136-050216-f23083f3018994e0e2c8cc518a650343-348750320")
    fun getInstallments(@Query("bin")bin:String,@Query("amount")amount : String) : Call<JsonArray>

    @POST("v1/card_tokens?public_key=TEST-5cd47bab-27d1-4732-8eac-e29e7397d7fa")
    fun createCardToken(@Body body: MercadoPagoCardTokenBody) : Call<JsonObject>
}