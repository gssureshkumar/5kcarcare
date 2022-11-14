package com.carcare.viewmodel.response.bookingDetails

import com.google.gson.annotations.SerializedName

data class Driver (

	@SerializedName("name") val name : String,
	@SerializedName("mobile") val mobile : Long
)