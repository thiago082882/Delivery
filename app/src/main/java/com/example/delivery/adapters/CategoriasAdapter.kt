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
import com.example.delivery.activities.client.products.list.ClientProductsListActivity
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.Category
import com.example.delivery.models.Rol
import com.example.delivery.utils.SharedPref




class CategoriasAdapter(val context: Activity, private val categorias : ArrayList<Category>) :
    RecyclerView.Adapter<CategoriasAdapter.CategoriasViewHolder>() {


    private val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.cardview_categorias, parent, false)

        return CategoriasViewHolder(view)

    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    override fun onBindViewHolder(holder:   CategoriasViewHolder, position: Int) {

        val category = categorias[position] // get the Rol object from the list

        holder.textviewCategory.text = category.name
//        Glide.with(context).load(category.image).into(holder.imageviewCategory)
        Glide.with(holder.itemView).load(category.image).into(holder.imageviewCategory)


        //call the click

        holder.itemView.setOnClickListener { goToProduct(category)}

    }
    private fun goToProduct(category: Category) {
            val i = Intent(context, ClientProductsListActivity::class.java)
        i.putExtra("idCategory",category.id)
            context.startActivity(i)


    }


    class CategoriasViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textviewCategory: TextView
        val imageviewCategory: ImageView

        init {
            textviewCategory = view.findViewById(R.id.tvCategory)
            imageviewCategory = view.findViewById(R.id.ivCategory)
        }
    }


}