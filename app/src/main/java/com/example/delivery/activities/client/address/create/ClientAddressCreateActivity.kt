package com.example.delivery.activities.client.address.create

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.delivery.R
import com.example.delivery.activities.client.address.list.ClientAddressListActivity
import com.example.delivery.activities.client.address.map.ClientAddressMapActivity
import com.example.delivery.models.Address
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.AddressProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressCreateActivity : AppCompatActivity() {

    var TAG = "AddressCreateActivity"
    var toolbar: Toolbar? = null
    var editTextRefPoint: EditText? = null
    var editTextAddress: EditText? = null
    var editTextNeighborhood: EditText? = null
    var btnCreateAddress: Button? = null
    var addressLat = 0.0
    var addressLng = 0.0
    var user: User? = null
    var sharedPref : SharedPref? = null
    var addressProvider : AddressProvider? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_address_create)

        sharedPref = SharedPref(this)

        getUserFromSession()

        addressProvider =  AddressProvider(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        editTextRefPoint = findViewById(R.id.edt_text_ref_point)
        editTextAddress = findViewById(R.id.edt_text_address)
        editTextNeighborhood = findViewById(R.id.edt_text_neighborhood)
        btnCreateAddress = findViewById(R.id.btn_create_address)


        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Novo Endereço"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        editTextRefPoint?.setOnClickListener { goToAddressMap() }
        btnCreateAddress?.setOnClickListener { createAddress() }
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }


    private fun createAddress() {
        val address = editTextAddress?.text.toString()
        val neighborhood = editTextNeighborhood?.text.toString()

        if (isValidForm(address,neighborhood)){

            val addressModel = Address(
                address= address,
                neighborhood = neighborhood,
                id_user  = user?.id!!,
                lat = addressLat,
                lng = addressLng
            )

            addressProvider?.create(addressModel)?.enqueue(object : Callback<ResponseHttp>{
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    if(response.body() != null){
                        Toast.makeText(this@ClientAddressCreateActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        goToAddressList()

                    }else {
                        Toast.makeText(this@ClientAddressCreateActivity, "Houve um erro na requisição", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(this@ClientAddressCreateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })


        }
    }

    private fun goToAddressList(){
        val i = Intent(this,ClientAddressListActivity::class.java)
        startActivity(i)
    }

    private fun isValidForm(address: String, neighborhood: String): Boolean {

        if (address.isNullOrBlank()) {
            Toast.makeText(this, "Digite o endereço", Toast.LENGTH_SHORT).show()
            return false
        }
        if (neighborhood.isNullOrBlank()) {
            Toast.makeText(this, "Digite seu bairro", Toast.LENGTH_SHORT).show()
            return false
        }

        if (addressLat == 0.0) {
            Toast.makeText(this, "Selecione o ponto de referência", Toast.LENGTH_SHORT).show()
            return false
        }
        if (addressLng == 0.0) {
            Toast.makeText(this, "Selecione o ponto de referência", Toast.LENGTH_SHORT).show()
            return false
        }
        return true

    }

    private var resultLauncher =

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val city = data?.getStringExtra("city")
                val address = data?.getStringExtra("address")
                val country = data?.getStringExtra("country")
                addressLat = data?.getDoubleExtra("lat", 0.0)!!
                addressLng = data?.getDoubleExtra("lng", 0.0)!!

                editTextRefPoint?.setText("$address $city")

                Log.d(TAG, "city: $city")
                Log.d(TAG, "address: $address")
                Log.d(TAG, "country: $country")
                Log.d(TAG, "lat: $addressLat")
                Log.d(TAG, "lng: $addressLng")

            }

        }

    private fun goToAddressMap() {

        val i = Intent(this, ClientAddressMapActivity::class.java)
        resultLauncher.launch(i)

    }
}