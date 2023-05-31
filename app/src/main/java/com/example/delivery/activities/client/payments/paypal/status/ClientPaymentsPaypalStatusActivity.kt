package com.example.delivery.activities.client.payments.paypal.status

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClientHomeActivity
import de.hdodenhof.circleimageview.CircleImageView

class ClientPaymentsPaypalStatusActivity : AppCompatActivity() {

    var textViewStatus: TextView? = null
    var circleImageStatus: CircleImageView? = null
    var buttonFinish: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_payments_status)

        textViewStatus = findViewById(R.id.textview_status)
        circleImageStatus = findViewById(R.id.circleimage_status)
        buttonFinish = findViewById(R.id.btn_finish)

        circleImageStatus?.setImageResource(R.drawable.ic_check)
        textViewStatus?.text = "Tu orden fue procesada exitosamente usando"

        buttonFinish?.setOnClickListener { goToHome() }

    }

    private fun goToHome() {
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }
}
