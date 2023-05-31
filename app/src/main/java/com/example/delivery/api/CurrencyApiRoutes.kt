package com.example.delivery.api


import com.example.delivery.routes.CurrencyRoutes
class CurrencyApiRoutes {

     val API_URL ="https://free.currconv.com/api/v7/"
     val retrofit = RetrofitClient()

    fun getCurrencyRoutes(): CurrencyRoutes {
        return retrofit.getClient(API_URL).create(CurrencyRoutes::class.java)
    }

}