package com.example.delivery.activities.client.payments.mercadopago.status

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClientHomeActivity
import de.hdodenhof.circleimageview.CircleImageView

class ClientPaymentsStatusActivity : AppCompatActivity() {

    var textViewStatus : TextView? = null
    var circleImageStatus : CircleImageView?=null
    var buttonFinish : Button? = null

    var paymentMethodId = " "
    var paymentStatus = ""
    var lastFourDigits = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payments_status)

        textViewStatus = findViewById(R.id.textview_status)
        circleImageStatus = findViewById(R.id.circleimage_status)
        buttonFinish = findViewById(R.id.btn_finish)

        paymentMethodId = intent.getStringExtra("paymentMethodId").toString()
        paymentStatus = intent.getStringExtra("paymentStatus").toString()
        lastFourDigits= intent.getStringExtra("lastFourDigits").toString()

        if(paymentStatus == "approved"){
            circleImageStatus?.setImageResource(R.drawable.ic_check)
            textViewStatus?.text = "A sua encomenda foi processada com sucesso usando ($paymentMethodId **** $lastFourDigits) \n\nConsulte o estado da sua compra na secção As minhas encomendas"
        }else {
            circleImageStatus?.setImageResource(R.drawable.ic_cancel)
            textViewStatus?.text = "Houve um erro ao processar o pagamento"
        }

        buttonFinish?.setOnClickListener { goToHome() }
    }
    private fun  goToHome(){
        val i = Intent(this,ClientHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)

    }
}