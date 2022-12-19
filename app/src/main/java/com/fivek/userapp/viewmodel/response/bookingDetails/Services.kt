package com.fivek.userapp.viewmodel.response.bookingDetails

import com.google.gson.annotations.SerializedName

data class Services (
	@SerializedName("banner") val banner : String,
	@SerializedName("name") val name : String
)