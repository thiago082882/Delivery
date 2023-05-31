package com.example.delivery.activities.client.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.delivery.R
import com.example.delivery.activities.MainActivity
import com.example.delivery.fragments.client.ClientCategoryFragment
import com.example.delivery.fragments.client.ClientOrdersFragment
import com.example.delivery.fragments.client.ClientProfileFragment
import com.example.delivery.models.User
import com.example.delivery.provider.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class ClientHomeActivity : AppCompatActivity() {

    private val TAG = "ClientHomeActivity"
    var sharedPref: SharedPref? = null

    var bottomNavigationView: BottomNavigationView? = null

    var userProvider : UsersProvider? = null
    var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)
        sharedPref = SharedPref(this)

        openFragment(ClientCategoryFragment())

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(ClientCategoryFragment())
                    true
                }
                R.id.item_orders -> {
                    openFragment(ClientOrdersFragment())
                    true
                }
                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                else -> false
            }
        }

        getUserFromSession()

        userProvider = UsersProvider(token = user?.sessionToken!!)
        createToken()
    }

    private fun createToken(){
        userProvider?.createToken(user!!,this)
    }

    private fun openFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }
}