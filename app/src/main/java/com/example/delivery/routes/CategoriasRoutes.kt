package com.example.delivery.routes

import com.example.delivery.models.Category
import com.example.delivery.models.ResponseHttp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CategoriasRoutes {


    @GET("categorias/getAll")
    fun getAll(
        @Header("Authorization") token: String
    ): Call<java.util.ArrayList<Category>>

    @Multipart
    @POST("categorias/create")
    fun create(
        @Part image: MultipartBody.Part,
        @Part("category") category: RequestBody,
        @Header("Authorization") token: String,
    ): Call<ResponseHttp>


}