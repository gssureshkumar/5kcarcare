package com.carcare.viewmodel.response.services

import com.google.gson.annotations.SerializedName
data class Offers (

	@SerializedName("banner") val banner : String,
	@SerializedName("service_id") val service_id : Int
)