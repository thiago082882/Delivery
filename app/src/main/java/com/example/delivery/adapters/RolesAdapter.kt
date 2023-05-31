package com.example.delivery.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClientHomeActivity
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.Rol
import com.example.delivery.utils.SharedPref




class RolesAdapter(val context: Activity, private val roles: ArrayList<Rol>) :
    RecyclerView.Adapter<RolesAdapter.RolesViewHolder>() {


    private val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.cardview_roles, parent, false)
        return RolesViewHolder(view)

    }

    override fun getItemCount(): Int {
        return roles.size
    }

    override fun onBindViewHolder(holder: RolesViewHolder, position: Int) {

        val rol = roles[position] // get the Rol object from the list

        holder.textviewRol.text = rol.name
        Glide.with(context).load(rol.image).into(holder.imageviewRol)


        //call the click

        holder.itemView.setOnClickListener { goToRol(rol) }

    }
    private fun goToRol(rol: Rol) {
        if (rol.name == "RESTAURANTE") {

            sharedPref.save("rol", "RESTAURANTE")

            val i = Intent(context, RestaurantHomeActivity::class.java)
            context.startActivity(i)
        }
        else if (rol.name == "CLIENTE") {
            sharedPref.save("rol", "CLIENTE")

            val i = Intent(context, ClientHomeActivity::class.java)
            context.startActivity(i)
        }
        else if (rol.name == "ENTREGADOR") {

            sharedPref.save("rol", "ENTREGADOR")

            val i = Intent(context, DeliveryHomeActivity::class.java)
            context.startActivity(i)
        }
    }


    class RolesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textviewRol: TextView
        val imageviewRol: ImageView

        init {
            textviewRol = view.findViewById(R.id.tvRol)
            imageviewRol = view.findViewById(R.id.ivRol)
        }
    }


}