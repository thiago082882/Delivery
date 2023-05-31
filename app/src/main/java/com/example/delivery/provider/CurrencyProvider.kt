package com.example.delivery.provider


import com.example.delivery.api.CurrencyApiRoutes
import com.example.delivery.routes.CurrencyRoutes
import com.google.gson.JsonObject
import retrofit2.Call


class CurrencyProvider() {

    private var currencyRoutes: CurrencyRoutes? = null

    init {
        val api = CurrencyApiRoutes()
        currencyRoutes = api.getCurrencyRoutes()
    }

    fun getCurrencyValue(): Call<JsonObject>? {
        return currencyRoutes?.getCurrencyValue()
    }


}