package com.example.delivery.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.client.address.list.ClientAddressListActivity
import com.example.delivery.models.Address
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson


class AddressAdapter(val context: Activity, private val address: ArrayList<Address>) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {


    private val sharedPref = SharedPref(context)
    val gson = Gson()
    var prev = 0
    var positionAddressSession = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.cardview_address, parent, false)

        return AddressViewHolder(view)

    }

    override fun getItemCount(): Int {
        return address.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {

        val a = address[position] // get the Rol object from the list

        if (!sharedPref.getData("address")
                .isNullOrBlank()
        ) { // Se o usuario selecionou um endereço da lista

            val adr = gson.fromJson(sharedPref.getData("address"), Address::class.java)

            if (adr.id == a.id) {

                positionAddressSession = position
                holder.imageviewCheck.visibility = View.VISIBLE

            }

        }

        holder.textviewAddress.text = a.address
        holder.textviewNeighborhood.text = a.neighborhood

        holder.itemView.setOnClickListener {

            (context as ClientAddressListActivity).resetValue(prev)
            (context as ClientAddressListActivity).resetValue(positionAddressSession)


            prev = position

            holder.imageviewCheck.visibility = View.VISIBLE
            saveAddress(a.toJson())

        }

    }


    private fun saveAddress(data: String) {

        val ad = gson.fromJson(data, Address::class.java)
        sharedPref.save("address", ad)

    }


    class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textviewAddress: TextView
        val textviewNeighborhood: TextView
        val imageviewCheck: ImageView

        init {
            textviewAddress = view.findViewById(R.id.textview_address)
            textviewNeighborhood = view.findViewById(R.id.textview_neighborhood)
            imageviewCheck = view.findViewById(R.id.ivCheck)
        }
    }


}