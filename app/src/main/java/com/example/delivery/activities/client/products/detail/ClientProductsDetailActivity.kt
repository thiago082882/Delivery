package com.example.delivery.activities.client.products.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.delivery.R
import com.example.delivery.models.Product
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ClientProductsDetailActivity : AppCompatActivity() {

     val TAG = "ProductsDetail"
    var product : Product? = null
    var gson = Gson()
    var imageSlider : ImageSlider?=null
    var textName :  TextView?=null
    var textDescription :  TextView?=null
    var textPrice :  TextView?=null
    var textCounter :  TextView?=null
    var ivAdd :  ImageView?=null
    private var ivRemove :  ImageView?=null
    var btnAdd : Button?=null


    var counter = 1
    var productPrice = 0.0

    var sharedPref : SharedPref? = null
    var selectProducts = ArrayList<Product>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_products_detail)

        product = gson.fromJson(intent.getStringExtra("product"),Product::class.java )
        sharedPref = SharedPref(this)

        imageSlider = findViewById(R.id.imageSlider)
        textName = findViewById(R.id.tvName)
        textDescription = findViewById(R.id.tvDescription)
        textPrice = findViewById(R.id.tvPrice)
        textCounter = findViewById(R.id.tvCounter)
        ivAdd = findViewById(R.id.ivAdd)
        ivRemove = findViewById(R.id.ivRemove)
        btnAdd = findViewById(R.id.btnAddProduct)

       val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(product?.image1,ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image2,ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(product?.image3,ScaleTypes.CENTER_CROP))

        imageSlider?.setImageList(imageList)

        textName?.text = product?.name
        textDescription?.text = product?.description
        textPrice?.text = "R$ ${product?.price}"

        ivAdd?.setOnClickListener { addItem() }
        ivRemove?.setOnClickListener {  removeItem() }
        btnAdd?.setOnClickListener { addToBag() }
        getProductsFromSharedPref()

    }

    private fun addToBag(){

        val index = getIndexOf(product?.id!!)
        if(index == -1 ){
            if(product?.quantity == null){
                product?.quantity =1
            }
            selectProducts.add(product!!)
        }else {
            selectProducts[index].quantity = counter
        }


        sharedPref?.save("order",selectProducts)
        Toast.makeText(this, "Produto Adicionado", Toast.LENGTH_SHORT).show()

    }

    private fun getProductsFromSharedPref(){
        if(!sharedPref?.getData("order").isNullOrBlank()){
            val type = object  : TypeToken<ArrayList<Product>>(){}.type
            selectProducts = gson.fromJson(sharedPref?.getData("order"),type)
            val index = getIndexOf(product?.id!!)


            if(index != -1){

                product?.quantity = selectProducts[index].quantity
                textCounter?.text = "${product?.quantity}"

                productPrice = product?.price!! * product?.quantity!!
                textPrice?.text = "R$ ${productPrice}"
                btnAdd?.text = "Editar produto"
                btnAdd?.backgroundTintList= ColorStateList.valueOf(Color.RED)

            }


            for (p in selectProducts){
                Log.d(TAG, "SharedPref: $p ")
            }

        }
    }
   //Verificar se o produto existe no shared e editar a quantidade
    private fun getIndexOf(idProduct : String): Int {
        var pos = 0
        for (p in selectProducts){
            if (p.id == idProduct){
                return pos
            }
            pos ++
        }
        return -1

    }


    private fun addItem(){
        counter++
        productPrice = product?.price!! * counter
        product?.quantity = counter
        textCounter?.text = "${product?.quantity}"
        textPrice?.text = "R$ ${productPrice}"


    }

    private fun removeItem(){
        if (counter > 1){
            counter--
            productPrice = product?.price!! * counter
            product?.quantity = counter
            textCounter?.text = "${product?.quantity}"
            textPrice?.text = "R$ ${productPrice}"
        }


    }
}