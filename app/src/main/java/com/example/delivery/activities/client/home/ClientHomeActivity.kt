package com.example.delivery.activities.client.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.delivery.R
import com.example.delivery.models.User
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson

class ClientHomeActivity : AppCompatActivity() {

    private val TAG = "ClientHomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)
        getUserFromSession()
    }

    private fun getUserFromSession() {

        val sharedPref = SharedPref(this)
        val gson = Gson()

        if (!sharedPref.getData("user").isNullOrBlank()) {
            //Se existe um Usuario
            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)
            Log.d(TAG, "Usuario: $user")
        }
    }


}