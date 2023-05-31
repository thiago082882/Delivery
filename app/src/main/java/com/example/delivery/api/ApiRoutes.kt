package com.example.delivery.api

import com.example.delivery.routes.AddressRoutes
import com.example.delivery.routes.CategoriasRoutes
import com.example.delivery.routes.OrdersRoutes
import com.example.delivery.routes.PaymentsRoutes
import com.example.delivery.routes.ProductsRoutes
import com.example.delivery.routes.UsersRoutes

class ApiRoutes {


    //val API_URL = "http://10.0.0.101:3000/api/"
    val API_URL = "https://delivery-backend-one.vercel.app/api/"
    val retrofit = RetrofitClient()

    fun getUsersRouters(): UsersRoutes {
        return retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }

    //chave JWT
    fun getUsersRoutersWithToken(token : String): UsersRoutes {
        return retrofit.getClientWithToken(API_URL,token).create(UsersRoutes::class.java)
    }

    //Category

    fun getCategoriasRouters(token : String): CategoriasRoutes {
        return retrofit.getClientWithToken(API_URL,token).create(CategoriasRoutes::class.java)
    }


    fun getProductsRoutes(token: String): ProductsRoutes {
        return retrofit.getClientWithToken(API_URL, token).create(ProductsRoutes::class.java)
    }


    fun getAddressRouters(token : String): AddressRoutes {
        return retrofit.getClientWithToken(API_URL,token).create(AddressRoutes::class.java)
    }

    fun getOrdersRouters(token : String): OrdersRoutes {
        return retrofit.getClientWithToken(API_URL,token).create(OrdersRoutes::class.java)
    }

    fun getPaymentsRouters(token : String): PaymentsRoutes {
        return retrofit.getClientWithToken(API_URL,token).create(PaymentsRoutes::class.java)
    }


}