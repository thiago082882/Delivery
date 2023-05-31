package com.example.delivery.provider

import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.Category
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.routes.CategoriasRoutes
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File

class CategoriasProvider(val token: String) {

    private var categoriasRoutes: CategoriasRoutes? = null

    init {
        val api = ApiRoutes()
        categoriasRoutes = api.getCategoriasRouters(token)
    }

    fun getAll(): Call<ArrayList<Category>>? {
        return categoriasRoutes?.getAll(token)
    }

    fun create(file: File, category: Category): Call<ResponseHttp>? {
        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("image", file.name, reqFile)
        val requestBody = category.toJson().toRequestBody("text/plain".toMediaTypeOrNull())
        return categoriasRoutes?.create(image, requestBody, token)
    }

}