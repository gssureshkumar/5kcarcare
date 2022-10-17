package com.carcare.viewmodel.response.servicesDetailsResponse

import com.google.gson.annotations.SerializedName

data class Reviews (

	@SerializedName("rating") val rating : Float,
	@SerializedName("review") val review : String,
	@SerializedName("name") val name : String
)