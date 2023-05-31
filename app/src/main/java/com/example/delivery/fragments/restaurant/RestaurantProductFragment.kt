package com.example.delivery.fragments.restaurant

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.delivery.R
import com.example.delivery.adapters.CategoriasAdapter
import com.example.delivery.models.Category
import com.example.delivery.models.Product
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.CategoriasProvider
import com.example.delivery.provider.ProductsProvider
import com.example.delivery.provider.UsersProvider
import com.example.delivery.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.tommasoberlose.progressdialog.ProgressDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import java.io.File

@Suppress("DEPRECATION")
class RestaurantProductFragment : Fragment() {

    val  TAG = "ProductFragment"
    var myView: View? = null
    var editName: EditText? = null
    var editDescription: EditText? = null
    var editPrice: EditText? = null
    var ivImageProduct1: ImageView? = null
    var ivImageProduct2: ImageView? = null
    var ivImageProduct3: ImageView? = null
    var btnCreate: Button? = null
    var spinner: Spinner? = null



    private var imageFile1: File? = null
    private var imageFile2: File? = null
    private var imageFile3: File? = null

    var sharedPref: SharedPref? = null
    var user: User? = null
    var categoriasProvider : CategoriasProvider?= null
    var productsProvider : ProductsProvider?=null
    var categories  = ArrayList<Category>()

    var idCategory = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_restaurant_product, container, false)


        editName = myView?.findViewById(R.id.edt_text_name_product)
        editDescription = myView?.findViewById(R.id.edt_text_description_product)
        editPrice = myView?.findViewById(R.id.edt_text_price_product)
        ivImageProduct1 = myView?.findViewById(R.id.iv_image1)
        ivImageProduct2 = myView?.findViewById(R.id.iv_image2)
        ivImageProduct3 = myView?.findViewById(R.id.iv_image3)
        btnCreate = myView?.findViewById(R.id.btn_create)
        spinner = myView?.findViewById(R.id.spinnerCategory)



        btnCreate?.setOnClickListener { createProduct() }
        ivImageProduct1?.setOnClickListener { selectImage(101) }
        ivImageProduct2?.setOnClickListener { selectImage(102) }
        ivImageProduct3?.setOnClickListener { selectImage(103) }

        sharedPref = SharedPref(requireActivity())

        sharedPref = SharedPref(requireActivity())

        getUserFromSession()

        categoriasProvider = CategoriasProvider(user?.sessionToken!!)
        productsProvider = ProductsProvider(user?.sessionToken!!)

        getCategorias()

        return myView

    }


    private fun getCategorias(){
        categoriasProvider?.getAll()?.enqueue(object : Callback<java.util.ArrayList<Category>> {
            override fun onResponse(
                call: Call<java.util.ArrayList<Category>>,
                response: Response<java.util.ArrayList<Category>>,
            ) {
                if(response.body() != null){
                    categories = response.body()!!

                    var arrayAdapter = ArrayAdapter<Category>(requireActivity(),android.R.layout.simple_dropdown_item_1line,categories)
                    spinner?.adapter = arrayAdapter
                    spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(adapterView : AdapterView<*>?, view: View?, position: Int, l: Long) {


                            idCategory = categories[position].id!!
                            Log.d(TAG, "id Category: $idCategory ")

                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }

                }
            }

            override fun onFailure(call: Call<java.util.ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "onFailure: ${t.message}", Toast.LENGTH_LONG).show()
            }


        })

    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }
    private fun createProduct() {

        val name = editName?.text.toString()
        val description = editDescription?.text.toString()
        val priceTxt = editPrice?.text.toString()
        val files = java.util.ArrayList<File>()


        if(isValidForm(name,description,priceTxt)){

            val product = Product(
                name = name ,
                description = description,
                price = priceTxt.toDouble(),
                id_category = idCategory
            )

            files.add(imageFile1!!)
            files.add(imageFile2!!)
            files.add(imageFile3!!)

            ProgressDialogFragment.showProgressBar(requireActivity())

            productsProvider?.create(files,product)?.enqueue(object : Callback<ResponseHttp>{
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    ProgressDialogFragment.hideProgressBar(requireActivity())
                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Body: ${response.body()}")
                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    ProgressDialogFragment.hideProgressBar(requireActivity())
                    Log.d(TAG, "onFailure: ${t.message}")
                    Toast.makeText(requireContext(), "onFailure: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })

        }

    }

    private fun resetForm (){
        editName?.setText("")
        editDescription?.setText("")
        editPrice?.setText("")
        imageFile1 = null
        imageFile2 = null
        imageFile3 = null
        ivImageProduct1?.setImageResource(R.drawable.ic_image)
        ivImageProduct2 ?.setImageResource(R.drawable.ic_image)
        ivImageProduct3?.setImageResource(R.drawable.ic_image)
    }
    private fun isValidForm(name : String,description : String, price : String) : Boolean{


        if (name.isNullOrBlank()){
            Toast.makeText(requireContext(), "Colocar o Nome do Produto", Toast.LENGTH_LONG).show()
            return false
        }
        if (description.isNullOrBlank()){
            Toast.makeText(requireContext(), "Colocar a descrição do Produto", Toast.LENGTH_LONG).show()
            return false
        }
        if (price.isNullOrBlank()){
            Toast.makeText(requireContext(), "Colocar o Preço do Produto", Toast.LENGTH_LONG).show()
            return false
        }
        if (imageFile1 == null){
            Toast.makeText(requireContext(), "Selecione a imagem  1", Toast.LENGTH_LONG).show()
            return false
        }
        if (imageFile2 == null){
            Toast.makeText(requireContext(), "Selecione a imagem  2", Toast.LENGTH_LONG).show()
            return false
        }
        if (imageFile3 == null){
            Toast.makeText(requireContext(), "Selecione a imagem  3", Toast.LENGTH_LONG).show()
            return false
        }
        if (idCategory.isNullOrBlank()){
            Toast.makeText(requireContext(), "Selecione a Categoria do Produto", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val fileUri = data?.data

            if (requestCode == 101) {
                imageFile1 = File(fileUri?.path) // EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                ivImageProduct1?.setImageURI(fileUri)
            }
            else if (requestCode == 102) {
                imageFile2 = File(fileUri?.path) // EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                ivImageProduct2?.setImageURI(fileUri)
            }

            else if (requestCode == 103) {
                imageFile3 = File(fileUri?.path) // EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                ivImageProduct3?.setImageURI(fileUri)
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun selectImage(requestCode: Int) {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(requestCode)

    }





}