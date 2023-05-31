package com.example.delivery.provider

import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.Category
import com.example.delivery.models.MercadoPagoPayment
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.routes.CategoriasRoutes
import com.example.delivery.routes.PaymentsRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class PaymentsProvider(val token: String) {

    private var paymentsRoutes: PaymentsRoutes? = null

    init {
        val api = ApiRoutes()
        paymentsRoutes = api.getPaymentsRouters(token)
    }



    fun create(mercadoPagoPayment: MercadoPagoPayment): Call<ResponseHttp>? {

        return paymentsRoutes?.createPayment(mercadoPagoPayment,token)

    }

}