package com.example.delivery.api

import com.example.delivery.routes.UsersRoutes

class ApiRoutes {


    val API_URL = "http://10.0.0.101:3000/api/"
    val retrofit = RetrofitClient()

    fun getUsersRouters(): UsersRoutes{
        return  retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }
}