package com.example.delivery.models

import com.google.gson.annotations.SerializedName

class User(
   @SerializedName("id") val id: String? = null,
   @SerializedName("name") val name: String,
   @SerializedName("lastname") val lastName: String,
   @SerializedName("email") val email: String,
   @SerializedName("phone") val phone: String,
   @SerializedName("password") val password: String,
   @SerializedName("image") val image: String? = null,
   @SerializedName("session_token") val sessionToken: String? = null,
   @SerializedName("isAvailable") val isAvailable: Boolean? = null,
   @SerializedName("roles")val roles : ArrayList<Rol>? = null,

){

    override fun toString(): String {
        return "User(id=$id, name='$name', lastName='$lastName', email='$email', phone='$phone', password='$password', image=$image, sessionToken=$sessionToken, isAvailable=$isAvailable, roles=$roles)"
    }
}


