package com.carcare.viewmodel.response.servicesDetailsResponse

import com.google.gson.annotations.SerializedName

data class ServiceDetailsResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Data
)