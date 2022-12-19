package com.fivek.userapp.viewmodel.response.services

import com.google.gson.annotations.SerializedName
data class Offers (

	@SerializedName("banner") val banner : String,
	@SerializedName("id") val id : Int
)