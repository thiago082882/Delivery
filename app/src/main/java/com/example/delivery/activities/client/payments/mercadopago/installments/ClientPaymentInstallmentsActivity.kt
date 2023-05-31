package com.example.delivery.activities.client.payments.mercadopago.installments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.*
import com.example.delivery.R
import com.example.delivery.activities.client.payments.mercadopago.status.ClientPaymentsStatusActivity
import com.example.delivery.models.Address
import com.example.delivery.models.MercadoPagoInstallments
import com.example.delivery.models.MercadoPagoPayment
import com.example.delivery.models.Order
import com.example.delivery.models.Payer
import com.example.delivery.models.Product
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.MercadoPagoProvider
import com.example.delivery.provider.PaymentsProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.tommasoberlose.progressdialog.ProgressDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientPaymentInstallmentsActivity : AppCompatActivity() {


    val TAG = "ClientPaymentsInst"
    var textViewTotal: TextView? = null
    var textViewInstallmentDescription: TextView? = null
    var buttonPay: Button? = null
    var spinnerInstallments: Spinner? = null

    var mercadoPagoProvider = MercadoPagoProvider()
     var paymentsProvider: PaymentsProvider? = null

    var cardToken = ""
    var firstSixDigits = ""

    var sharedPref: SharedPref? = null
    var user: User? = null
    var selectedProducts = ArrayList<Product>()
    var gson = Gson()

    var total = 0.0

    var installmentsSelected = "" // COUTA SELECCIONA 1, 10

    var address: Address? = null

    var paymentMethodId = ""
    var paymentTypeId = ""
    var issuerId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payment_installments)


        sharedPref = SharedPref(this)

         getUserFromSession()
         getAddressFromSession()

        paymentsProvider = PaymentsProvider(user?.sessionToken!!)

        cardToken = intent.getStringExtra("cardToken").toString()
        firstSixDigits = intent.getStringExtra("firstSixDigits").toString()


        textViewTotal = findViewById(R.id.textview_total)
        textViewInstallmentDescription = findViewById(R.id.textview_installments_description)
        buttonPay = findViewById(R.id.btn_pay)
        spinnerInstallments = findViewById(R.id.spinner_installments)

        getProductsFromSharedPref()
        getInstallments()

        buttonPay?.setOnClickListener {

            if (!installmentsSelected.isNullOrBlank()) {
                createPayment()
            }
            else {
                Toast.makeText(this, "Deverá selecionar o número de parcelas", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun createPayment(){

        val order = Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress = address?.id!!
        )

        val payer = Payer(email = user?.email!!)

        val mercadoPagoPayment = MercadoPagoPayment(
            order = order,
            token = cardToken,
            description = "com.example.delivery",
            paymentMethodId = paymentMethodId,
            paymentTypeId = paymentTypeId,
            issuerId = issuerId,
            payer = payer,
            transactionAmount = total,
            installments = installmentsSelected.toInt()
        )

        ProgressDialogFragment.showProgressBar(this)

        paymentsProvider?.create(mercadoPagoPayment)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                ProgressDialogFragment.hideProgressBar(this@ClientPaymentInstallmentsActivity)
                if (response.body() != null) {

                    if (response.body()?.isSuccess == true) {
                        sharedPref?.remove("order")
                    }

                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Body: ${response.body()}")

                    Toast.makeText(this@ClientPaymentInstallmentsActivity, response.body()?.message, Toast.LENGTH_LONG)
                        .show()

                    val status = response.body()?.data?.get("status")?.asString
                    val lastFour = response.body()?.data?.get("card")?.asJsonObject?.get("last_four_digits")?.asString

                   goToPaymentsStatus(paymentMethodId, status!!, lastFour!!)
                }
                else {
                   goToPaymentsStatus(paymentMethodId, "denied", "")
                    Toast.makeText(this@ClientPaymentInstallmentsActivity, "No hubo una respuesta exitosa", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                ProgressDialogFragment.hideProgressBar(this@ClientPaymentInstallmentsActivity)
                Toast.makeText(this@ClientPaymentInstallmentsActivity, "Error: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }

        })

    }

    private fun goToPaymentsStatus(paymentMethodId: String, paymentStatus: String, lastFourDigits: String) {
        val i = Intent(this, ClientPaymentsStatusActivity::class.java)
        i.putExtra("paymentMethodId", paymentMethodId)
        i.putExtra("paymentStatus", paymentStatus)
        i.putExtra("lastFourDigits", lastFourDigits)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun getInstallments() {
        mercadoPagoProvider.getInstallments(firstSixDigits, "$total")?.enqueue(object:
            Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>
            ) {

                if (response.body() != null) {

                    val jsonInstallments = response.body()!!.get(0).asJsonObject.get("payer_costs").asJsonArray

                    val type = object: TypeToken<ArrayList<MercadoPagoInstallments>>() {}.type
                    val installments = gson.fromJson<ArrayList<MercadoPagoInstallments>>(jsonInstallments, type)

                    paymentMethodId = response.body()?.get(0)?.asJsonObject?.get("payment_method_id")?.asString!!
                    paymentTypeId = response.body()?.get(0)?.asJsonObject?.get("payment_type_id")?.asString!!
                    issuerId = response.body()?.get(0)?.asJsonObject?.get("issuer")?.asJsonObject?.get("id")?.asString!!

                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "installments: $installments")

                    val arrayAdapter = ArrayAdapter<MercadoPagoInstallments>(this@ClientPaymentInstallmentsActivity, android.R.layout.simple_dropdown_item_1line, installments)
                    spinnerInstallments?.adapter = arrayAdapter
                    spinnerInstallments?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, l: Long) {
                            installmentsSelected = "${installments[position].installments}"
                            Log.d(TAG, "Parcelas selecionadas: $installmentsSelected")
                            textViewInstallmentDescription?.text = installments[position].recommendedMessage
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }
                    }
                }

            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Toast.makeText(this@ClientPaymentInstallmentsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getProductsFromSharedPref() {

        if (!sharedPref?.getData("order").isNullOrBlank()) { // EXISTE UNA ORDEN EN SHARED PREF
            val type = object: TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)

            for (p in selectedProducts) {
                total += (p.price * p.quantity!!)
            }

            textViewTotal?.text = "R$ ${total}"
        }

    }
    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }
    private fun getAddressFromSession() {

        if (!sharedPref?.getData("address").isNullOrBlank()) {
            address = gson.fromJson(sharedPref?.getData("address"), Address::class.java) // SI EXISTE
        }

    }
}