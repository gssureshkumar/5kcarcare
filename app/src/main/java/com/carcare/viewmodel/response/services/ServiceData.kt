package com.carcare.viewmodel.response.services

import com.google.gson.annotations.SerializedName

data class ServiceData (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("thumbnail") val thumbnail : String
)