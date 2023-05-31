package com.example.delivery.fragments.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.delivery.adapters.CategoriasAdapter
import com.example.delivery.models.Category
import com.example.delivery.models.User
import com.example.delivery.provider.CategoriasProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class ClientCategoryFragment : Fragment() {
    val  TAG = "CategoryFragment"
    var myView: View? = null
    var rvCategory: RecyclerView? = null
    var categoriasProvider: CategoriasProvider? = null
    var adapter: CategoriasAdapter? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var categorias = ArrayList<Category>()
    var toolbar  : androidx.appcompat.widget.Toolbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_client_category, container, false)
         setHasOptionsMenu(true)

        //Toolbar

        toolbar = myView?.findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        toolbar?.title = "Categorias"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)


        rvCategory = myView?.findViewById(R.id.rvCategory)
        rvCategory?.layoutManager = LinearLayoutManager(requireContext())

        sharedPref = SharedPref(requireActivity())

         getUserFromSession()

        categoriasProvider = CategoriasProvider(user?.sessionToken!!)

        getCategorias()

        return myView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shopping_bag,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.item_shopping_bag){
            goToShoppingBag()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToShoppingBag(){
        val i = Intent(requireContext(),ClientShoppingBagActivity::class.java)
        startActivity(i)
    }

    private fun getCategorias(){
        categoriasProvider?.getAll()?.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>,
            ) {
               if(response.body() != null){
                   categorias = response.body()!!
                   adapter = CategoriasAdapter(requireActivity(),categorias)
                   rvCategory?.adapter= adapter
               }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
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

}