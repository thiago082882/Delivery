package com.example.delivery.routes


import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface CurrencyRoutes {

    @GET("convert?q=BRL_USD&compact=ultra&apiKey=1a20ebe38f177013d5c6")
    fun getCurrencyValue(): Call<JsonObject>


}