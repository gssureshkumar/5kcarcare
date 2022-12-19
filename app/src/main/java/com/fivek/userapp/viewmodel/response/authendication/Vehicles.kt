package com.fivek.userapp.viewmodel.response.authendication

import com.google.gson.annotations.SerializedName

data class Vehicles (

	@SerializedName("id") val id : String,
	@SerializedName("userid") val userid : String,
	@SerializedName("model") val model : String,
	@SerializedName("type") val type : String,
	@SerializedName("reg_no") val reg_no : String,
	@SerializedName("primary") val primary : Boolean,
	@SerializedName("thumbnail") val thumbnail : String
)