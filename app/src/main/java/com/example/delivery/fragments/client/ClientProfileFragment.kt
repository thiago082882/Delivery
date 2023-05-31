package com.example.delivery.fragments.client

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.MainActivity
import com.example.delivery.activities.SelectRoleActivity
import com.example.delivery.activities.client.update.ClientUpdateActivity
import com.example.delivery.models.User
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView


class ClientProfileFragment : Fragment() {

    var myView: View? = null
    var buttonSelectRol: Button? = null
    var buttonUpdateProfile: Button? = null
    var circleImageUser: CircleImageView? = null
    var tvName: TextView? = null
    var tvEmail: TextView? = null
    var tvPhone: TextView? = null
    var ivLogout: ImageView? = null

    var sharedPref: SharedPref? = null
    var user: User? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        myView = inflater.inflate(R.layout.fragment_client_profile, container, false)

        sharedPref = SharedPref(requireActivity())

        buttonSelectRol = myView?.findViewById(R.id.btn_select_rol)
        buttonUpdateProfile = myView?.findViewById(R.id.btn_update_profile)
        tvName = myView?.findViewById(R.id.tvName)
        tvEmail = myView?.findViewById(R.id.tvEmail)
        tvPhone = myView?.findViewById(R.id.tvPhone)
        circleImageUser = myView?.findViewById(R.id.circle_image_user)
        ivLogout = myView?.findViewById(R.id.ivLogout)



        buttonSelectRol?.setOnClickListener { goToSelectRol() }
        ivLogout?.setOnClickListener { logout() }
        buttonUpdateProfile?.setOnClickListener { goToUpdate() }

        getUserFromSession()

        tvName?.text = "${user?.name} ${user?.lastname}"
        tvEmail?.text = user?.email
        tvPhone?.text = user?.phone
        if (!user?.image.isNullOrBlank()) {
            Glide.with(requireContext()).load(user?.image).into(circleImageUser!!)
        }


        return myView
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }

    }

    private fun goToUpdate() {
        val i = Intent(requireContext(), ClientUpdateActivity::class.java)
        startActivity(i)
    }

    private fun goToSelectRol() {
        val i = Intent(requireContext(), SelectRoleActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun logout() {
        sharedPref?.remove("user")
        val i = Intent(requireContext(), MainActivity::class.java)
        startActivity(i)
    }

}