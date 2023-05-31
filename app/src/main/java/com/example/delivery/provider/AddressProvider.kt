package com.example.delivery.provider

import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.Address
import com.example.delivery.models.ResponseHttp
import com.example.delivery.routes.AddressRoutes
import retrofit2.Call

class AddressProvider(val token: String) {

    private var addressRoutes: AddressRoutes? = null

    init {
        val api = ApiRoutes()
        addressRoutes = api.getAddressRouters(token)
    }

    fun getAddress(idUser : String): Call<ArrayList<Address>>? {
        return addressRoutes?.getAddress(idUser,token)
    }

    fun create(address: Address): Call<ResponseHttp>? {
        return addressRoutes?.create(address,token)

    }

}