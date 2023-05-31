package com.example.delivery.activities.client.payments.paypal.form

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.example.delivery.R
import com.example.delivery.activities.client.payments.paypal.status.ClientPaymentsPaypalStatusActivity
import com.example.delivery.adapters.ShoppingBagAdapter
import com.example.delivery.config.Config
import com.example.delivery.models.Address
import com.example.delivery.models.Product
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.CurrencyProvider
import com.example.delivery.provider.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PayPalButton
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode



class ClientPaymentsPaypalFormActivity : AppCompatActivity() {

    private lateinit var textViewAmount: TextView
    private lateinit var textViewUsd: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var paypalButton: PayPalButton
    private lateinit var ordersProvider: OrdersProvider
    private var user: User? = null
    private lateinit var sharedPref: SharedPref
    private val gson = Gson()
    private var selectedProducts = ArrayList<Product>()
    private var address: Address? = null
    private val currencyProvider = CurrencyProvider()
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payments_paypal_form)

        sharedPref = SharedPref(this)
        getUserFromSession()
        getAddressFromSession()
        ordersProvider = OrdersProvider(user?.sessionToken!!)
        toolbar = findViewById(R.id.toolbar)
        textViewAmount = findViewById(R.id.textview_amount)
        textViewUsd = findViewById(R.id.textview_usd)
        paypalButton = findViewById(R.id.payPalButton)

        getProductsFromSharedPref()

        toolbar.apply {
            setTitleTextColor(ContextCompat.getColor(this@ClientPaymentsPaypalFormActivity, R.color.black))
            title = "Processo de pagamento"
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        getCurrencyValue()
        setupPayPalButton()
    }

    private fun setupPayPalButton() {
        paypalButton.setup(
            createOrder = createOrderAction(),
            onApprove = onApproveAction(),
            onCancel = onCancelAction(),
            onError = onErrorAction()
        )
    }

    private fun createOrderAction(): CreateOrder = CreateOrder { createOrderActions ->
        val order = Order(
            intent = OrderIntent.CAPTURE,
            appContext = AppContext(
                userAction = UserAction.PAY_NOW
            ),
            purchaseUnitList = listOf(
                PurchaseUnit(
                    amount = Amount(
                        currencyCode = CurrencyCode.BRL,
                        value = total.toString()
                    )
                )
            )
        )

        createOrderActions.create(order)
    }

    private fun onApproveAction(): OnApprove = OnApprove { approval ->
        approval.orderActions.capture { captureOrderResult ->
            if (captureOrderResult.toString().contains("Success")) {
                Toast.makeText(this, "pagamento aprovadoo", Toast.LENGTH_LONG).show()
                createOrder()
            }
            Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
        }
    }

    private fun onCancelAction(): OnCancel = OnCancel {
        Log.d("OnCancel", "Buyer canceled the PayPal experience.")
    }

    private fun onErrorAction(): OnError = OnError { errorInfo ->
        Log.d("OnError", "Error: $errorInfo")
    }
    private fun getCurrencyValue() {
        currencyProvider.getCurrencyValue()?.enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    val usd = response.body()?.get("BRL_USD")?.asDouble
                    if (usd != null) {
                        val totalUsd = BigDecimal(total / usd).setScale(2, RoundingMode.HALF_EVEN)
                        textViewUsd.text = "$totalUsd$"
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(
                    this@ClientPaymentsPaypalFormActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun goToPaypalStatus() {
        val intent = Intent(this, ClientPaymentsPaypalStatusActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun createOrder() {
        val order = com.example.delivery.models.Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress = address?.id!!
        )

        ordersProvider.create(order)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.isSuccess == true) {
                        sharedPref.remove("order")
                        goToPaypalStatus()
                    }
                    Toast.makeText(
                        this@ClientPaymentsPaypalFormActivity,
                        "${response.body()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@ClientPaymentsPaypalFormActivity,
                        "Ocorreu um erro no pedido",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@ClientPaymentsPaypalFormActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getUserFromSession() {
        val gson = Gson()
        val userData = sharedPref.getData("user")
        if (!userData.isNullOrBlank()) {
            user = gson.fromJson(userData, User::class.java)
        }
    }

    private fun getProductsFromSharedPref() {
        val orderData = sharedPref.getData("order")
        if (!orderData.isNullOrBlank()) {
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(orderData, type)
            for (p in selectedProducts) {
                total += p.price * p.quantity!!
            }
            textViewAmount.text = "R$ $total"
        }
    }

    private fun getAddressFromSession() {
        val addressData = sharedPref.getData("address")
        if (!addressData.isNullOrBlank()) {
            address = gson.fromJson(addressData, Address::class.java)
        } else {
            Toast.makeText(
                this,
                "Selecione um endereço para continuar",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}