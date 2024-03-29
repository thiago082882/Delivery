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
import com.example.delivery.activities.client.products.detail.ClientProductsDetailActivity
import com.example.delivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.Category
import com.example.delivery.models.Product
import com.example.delivery.models.Rol
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson


class ShoppingBagAdapter(val context: Activity, val products: ArrayList<Product>) :
    RecyclerView.Adapter<ShoppingBagAdapter.ShoppingBagViewHolder>() {

    val sharedPref = SharedPref(context)

    init {
        (context as ClientShoppingBagActivity).setTotal(getTotal())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingBagViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_shopping_bag, parent, false)
        return ShoppingBagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ShoppingBagViewHolder, position: Int) {

        val product = products[position] // CADA UNA DE LAS CATEGORIAS

        holder.textViewName.text = product.name
        holder.textViewCounter.text = "${product.quantity}"
        if(product.quantity != null){
            holder.textViewPrice.text = "R$ ${product.price * product.quantity!!}"
        }

        Glide.with(holder.imageViewProduct).load(product.image1).into(holder.imageViewProduct)
        holder.imageViewAdd.setOnClickListener { addItem(product, holder) }
        holder.imageViewRemove.setOnClickListener { removeItem(product, holder) }
        holder.imageViewDelete.setOnClickListener { deleteItem(position) }

        //holder.itemView.setOnClickListener { goToDetail(product) }

    }

    private fun getTotal(): Double {
        var total = 0.0
        for (p in products) {
            if (p.quantity != null) {

                total = total + (p.quantity!! * p.price)

            }

        }
        return total
    }

    private fun getIndexOf(idProduct: String): Int {
        var pos = 0
        for (p in products) {
            if (p.id == idProduct) {
                return pos
            }
            pos++
        }
        return -1

    }

    private fun deleteItem(position: Int) {

        products.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, products.size)
        sharedPref.save("order", products)
        (context as ClientShoppingBagActivity).setTotal(getTotal())


    }


    private fun addItem(product: Product, holder: ShoppingBagViewHolder) {

        val index = getIndexOf(product.id!!)
        product.quantity = product.quantity!! + 1
        products[index].quantity = product.quantity

        holder.textViewCounter.text = "${product.quantity}"
        holder.textViewPrice?.text = "R$ ${product.quantity!! * product.price}"

        sharedPref.save("order", products)
        (context as ClientShoppingBagActivity).setTotal(getTotal())


    }

    private fun removeItem(product: Product, holder: ShoppingBagViewHolder) {

        if (product.quantity!! > 1) {
            val index = getIndexOf(product.id!!)
            product.quantity = product.quantity!! - 1
            products[index].quantity = product.quantity

            holder.textViewCounter.text = "${product.quantity}"
            holder.textViewPrice?.text = "R$ ${product.quantity!! * product.price}"
            (context as ClientShoppingBagActivity).setTotal(getTotal())

            sharedPref.save("order", products)
        }

    }


    private fun goToDetail(product: Product) {
        val i = Intent(context, ClientProductsDetailActivity::class.java)
        i.putExtra("product", product.toJson())
        context.startActivity(i)

    }


    class ShoppingBagViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textViewName: TextView
        val textViewPrice: TextView
        val textViewCounter: TextView
        val imageViewProduct: ImageView
        val imageViewAdd: ImageView
        val imageViewRemove: ImageView
        val imageViewDelete: ImageView

        init {
            textViewName = view.findViewById(R.id.tvNameProduct)
            textViewPrice = view.findViewById(R.id.tvPrice)
            textViewCounter = view.findViewById(R.id.tvCounter)
            imageViewProduct = view.findViewById(R.id.ivProduct)
            imageViewAdd = view.findViewById(R.id.ivAdd)
            imageViewRemove = view.findViewById(R.id.ivRemove)
            imageViewDelete = view.findViewById(R.id.ivDelete)
        }

    }

}