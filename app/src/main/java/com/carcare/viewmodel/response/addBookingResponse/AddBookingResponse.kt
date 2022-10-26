package com.carcare.viewmodel.response.addBookingResponse

import com.google.gson.annotations.SerializedName

data class AddBookingResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Data
)