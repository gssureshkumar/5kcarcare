package com.fivek.userapp.viewmodel.response.outlets

import com.google.gson.annotations.SerializedName

data class Outlet (

	@SerializedName("id") val id : Int,
	@SerializedName("mobileNumber") val mobileNumber : String,
	@SerializedName("name") val name : String,
	@SerializedName("rating") val rating : Int,
	@SerializedName("elite") val elite : Boolean,
	@SerializedName("lat") val lat : Double,
	@SerializedName("long") val long : Double,
	@SerializedName("address") val address : String,
	@SerializedName("city") val city : String,
	@SerializedName("state") val state : String,
	@SerializedName("selected") var selected : Boolean
)