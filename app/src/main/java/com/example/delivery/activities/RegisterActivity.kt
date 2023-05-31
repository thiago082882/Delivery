package com.example.delivery.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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

class RegisterActivity : AppCompatActivity() {

    val TAG = "RegisterActivity"

    var imageViewGoToLogin: ImageView? = null
    var edtTextName: EditText? = null
    var edtTextLastName: EditText? = null
    var edtTextEmail: EditText? = null
    var edtTextPhone: EditText? = null
    var edtTextPass: EditText? = null
    var edtTextConfirmedPass: EditText? = null
    var buttonRegister: Button? = null


    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        imageViewGoToLogin = findViewById(R.id.imageview_go_to_login)
        edtTextName=findViewById(R.id.edt_text_name)
        edtTextLastName=findViewById(R.id.edt_text_lastname)
        edtTextEmail = findViewById(R.id.edt_text_email)
        edtTextPhone=findViewById(R.id.edt_text_phone)
        edtTextPass = findViewById(R.id.edt_text_password)
        edtTextConfirmedPass=findViewById(R.id.edt_text_confirmedPassword)
        buttonRegister = findViewById(R.id.btn_register)

        imageViewGoToLogin?.setOnClickListener {
            goToLogin()

        }

        buttonRegister?.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val name = edtTextName?.text.toString()
        val lastname = edtTextLastName?.text.toString()
        val email = edtTextEmail?.text.toString()
        val phone = edtTextPhone?.text.toString()
        val password = edtTextPass?.text.toString()
        val confirmedPass = edtTextConfirmedPass?.text.toString()

        if(isValidForm(name,lastname,email,phone,password,confirmedPass)){

            val user = User(

                name= name,
                lastname=lastname,
                email=email,
                phone=phone,
                password= password



            )

            usersProvider.register(user)?.enqueue(object : Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    if(response.body()?.isSuccess==true){
                        saveUserInSession(response.body()?.data.toString())
                        goToClientHome()
                    }
                    Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG,"Response: ${response}")
                    Log.d(TAG,"Body: ${response.body()}")
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG,"Ocorreu um error ${t.message}")
                    Toast.makeText(this@RegisterActivity, "Ocorreu um error ${t.message}", Toast.LENGTH_LONG).show()
                }


            } )
        }


    }
    private fun goToClientHome() {
        val i = Intent(this, SaveImageActivity::class.java)
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
    private fun isValidForm (
        name: String,
        lastname: String,
        email : String,
        phone : String,
        password : String,
        confirmedPass : String
    ): Boolean{

        if(name.isBlank()){
            Toast.makeText(this, "Necessita Colocar um Nome ", Toast.LENGTH_SHORT).show()
            return false
        }
        if(lastname.isBlank()){
            Toast.makeText(this, "Necessita Colocar um Sobrenome ", Toast.LENGTH_SHORT).show()
            return false
        }

        if(email.isBlank()){
            Toast.makeText(this, "Necessita Colocar um Email  ", Toast.LENGTH_SHORT).show()
            return false
        }
        if(phone.isBlank()){
            Toast.makeText(this, "Necessita Colocar um Telefone ", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password.isBlank()){
            Toast.makeText(this, "Necessita Colocar uma Senha ", Toast.LENGTH_SHORT).show()
            return false
        }
        if(confirmedPass.isBlank()){
            Toast.makeText(this, "Necessita Colocar uma confirmação de Senha ", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!email.isEmailValid()){
            return false

        }
        if (password != confirmedPass){
            Toast.makeText(this, "As Senhas não coincidem ", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun goToLogin() {

        val i = Intent(this, MainActivity::class.java)
        startActivity(i)

    }
}