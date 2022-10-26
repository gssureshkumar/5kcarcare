package com.carcare.viewmodel.response.bookingList

import com.google.gson.annotations.SerializedName

data class Services (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("thumbnail") val thumbnail : String
)