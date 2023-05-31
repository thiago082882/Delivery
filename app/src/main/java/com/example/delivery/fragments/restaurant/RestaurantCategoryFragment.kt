package com.example.delivery.fragments.restaurant

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.delivery.R
import com.example.delivery.models.Category
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.provider.CategoriasProvider
import com.example.delivery.utils.SharedPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class RestaurantCategoryFragment : Fragment() {
    val TAG = "Category"
    var myView: View? = null
    var imageViewCategory: ImageView? = null
    var edtTextCategory: EditText? = null
    var btnCreateCategory: Button? = null

    private var imageFile: File? = null
    var categoriasProvider: CategoriasProvider? = null
    var sharedPref: SharedPref? = null
    var user: User? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_restaurant_category, container, false)

        sharedPref = SharedPref(requireActivity())

        imageViewCategory = myView?.findViewById(R.id.ivCategory)
        edtTextCategory = myView?.findViewById(R.id.edt_text_category)
        btnCreateCategory = myView?.findViewById(R.id.btn_category)

        imageViewCategory?.setOnClickListener { selectImage() }

        btnCreateCategory?.setOnClickListener { createCategory() }

        getUserFromSession()
        categoriasProvider = CategoriasProvider(user?.sessionToken!!)

        return myView
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }

    private fun createCategory() {
        val name = edtTextCategory?.text.toString()

        if (imageFile != null) {

            val category = Category(name = name)

            categoriasProvider?.create(imageFile!!, category)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d(TAG, "RESPONSE: $response")
                    Log.d(TAG, "BODY: ${response.body()}")

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()

                    if(response.body()?.isSuccess == true ){

                        clearForm()

                    }

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG).show()
                }

            })

        }
        else {
            Toast.makeText(requireContext(), "Seleciona uma imagem", Toast.LENGTH_SHORT).show()
        }

    }

    private fun clearForm() {
        edtTextCategory?.setText("")
        imageFile = null
        imageViewCategory?.setImageResource(R.drawable.ic_image)
    }

    private val startImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data
                imageFile =
                    File(fileUri?.path) // EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                imageViewCategory?.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Tarefa cancelada ", Toast.LENGTH_LONG).show()
            }

        }

    private fun selectImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }
    }


}