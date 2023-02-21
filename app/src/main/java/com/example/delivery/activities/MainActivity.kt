package com.example.delivery.activities

import android.content.Intent
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
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    var imageViewGoToRegister: ImageView? = null
    var edtTextEmail: EditText? = null
    var edtTextPass: EditText? = null
    var buttonLogin: Button? = null
    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewGoToRegister = findViewById(R.id.imageview_go_to_register)
        edtTextEmail = findViewById(R.id.edit_text_email)
        edtTextPass = findViewById(R.id.edit_text_password)
        buttonLogin = findViewById(R.id.btn_login)

        imageViewGoToRegister?.setOnClickListener {
            goToRegister()
        }
        buttonLogin?.setOnClickListener { login() }
        getUserFromSession()
    }

    private fun login() {
        val email = edtTextEmail?.text.toString()
        val password = edtTextPass?.text.toString()

        if(isValidForm(email,password)){

            usersProvider.login(email, password)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d("Main", "Response : ${response.body()}")

                    if(response.body()?.isSuccess == true){
                        Toast.makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        saveUserInSession(response.body()?.data.toString())
                        goToClientHome()

                     }else {
                        Toast.makeText(this@MainActivity, "Os dados não estão corretos ", Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d("Main", "Houve um Erro ${t.message}")
                    Toast.makeText(this@MainActivity, "Houve um Erro ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

            }
        else {
            Toast.makeText(this, "O Formulario não é Válido ", Toast.LENGTH_LONG).show()
        }

//        Log.d("Main", "A senha é: $password")
    }

    private fun goToClientHome() {
        val i = Intent(this, ClientHomeActivity::class.java)
        startActivity(i)
    }


    private fun saveUserInSession(data: String) {
        val sharedPref = SharedPref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }


fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

    private fun getUserFromSession() {

        val sharedPref = SharedPref(this)
        val gson = Gson()

        if (!sharedPref.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)
            goToClientHome()
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
       val i = Intent(this,RegisterActivity::class.java)
        startActivity(i)
    }
}