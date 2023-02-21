package com.example.delivery.provider

import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.routes.UsersRoutes
import retrofit2.Call

class UsersProvider {

    private var usersRoutes : UsersRoutes? = null

    init{
        val api = ApiRoutes()
        usersRoutes = api.getUsersRouters()

    }

    fun register(user: User):Call<ResponseHttp>?{

        return  usersRoutes?.register(user)
    }

    fun login(email:String,password:String):Call<ResponseHttp>?{

        return  usersRoutes?.login(email, password)
    }
}