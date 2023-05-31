package com.example.delivery.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClientHomeActivity
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private var imageViewGoToRegister: ImageView? = null
    private var edtTextEmail: EditText? = null
    private var edtTextPass: EditText? = null
    private var buttonLogin: Button? = null
    private var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewGoToRegister = findViewById(R.id.imageview_go_to_register)
        edtTextEmail = findViewById(R.id.edit_text_email)
        edtTextPass = findViewById(R.id.edit_text_password)
        buttonLogin = findViewById(R.id.btn_login)

        imageViewGoToRegister?.setOnClickListener { goToRegister() }
        buttonLogin?.setOnClickListener { login() }
        getUserFromSession()
    }

    private fun login() {
        val email = edtTextEmail?.text.toString()
        val password = edtTextPass?.text.toString()

        if (isValidForm(email, password)) {

            usersProvider.login(email, password)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>,
                ) {

                    Log.d("Main", "Response : ${response.body()}")

                    if (response.body()?.isSuccess == true) {
                        Toast.makeText(this@MainActivity,
                            response.body()?.message,
                            Toast.LENGTH_LONG).show()
                        saveUserInSession(response.body()?.data.toString())


                    } else {
                        Toast.makeText(this@MainActivity,
                            "Os dados não estão corretos ",
                            Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d("Main", "Houve um Erro ${t.message}")
                    Toast.makeText(this@MainActivity,
                        "Houve um Erro ${t.message}",
                        Toast.LENGTH_LONG).show()
                }
            })

        } else {
            Toast.makeText(this, "O Formulario não é Válido ", Toast.LENGTH_LONG).show()
        }

//        Log.d("Main", "A senha é: $password")
    }

    private fun goToClientHome() {
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK //DELETE SCREEN HISTORY
        startActivity(i)
    }

    private fun goToRestaurantHome() {
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun goToDeliveryHome() {
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun goToSelectRol() {
        val i = Intent(this, SelectRoleActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }


    private fun saveUserInSession(data: String) {
        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)

        if (user.roles?.size!! > 1) { //if user has more than one role

            goToSelectRol()

        } else { //if user has only one role
            goToClientHome()
        }
    }


    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    private fun getUserFromSession() {

        val sharedPref = SharedPref(this)
        val gson = Gson()

        if (!sharedPref.getData("user").isNullOrBlank()) {
            // IF THE USER EXISTS IN THE SESSION
            val user =  gson.fromJson(sharedPref.getData("user"), User::class.java)

            if (!sharedPref.getData("rol").isNullOrBlank()) {
                //IF THE USER SELECT IN THE SESSION ROL
                val rol = sharedPref.getData("rol")?.replace("\"", "")
                Log.d("MainActivity", "ROL $rol")
                if (rol == "RESTAURANTE") {
                    goToRestaurantHome()
                } else if (rol == "CLIENTE") {
                    goToClientHome()
                } else if (rol == "ENTREGADOR") {
                    goToDeliveryHome()
                }
            } else {
                Log.d("MainActivity", "FUNÇÃO NÃO EXISTE")
                goToClientHome()
            }
        }

    }

    private fun isValidForm(email: String, password: String): Boolean {

        if (email.isBlank()) {
            return false
        }

        if (password.isBlank()) {
            return false
        }

        if (!email.isEmailValid()) {
            return false
        }

        return true
    }

    private fun goToRegister() {
        val i = Intent(this, RegisterActivity::class.java)
        startActivity(i)
    }
}