package com.fivek.userapp.viewmodel.response.bookingDetails

import com.google.gson.annotations.SerializedName

data class BookingDetailsResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Data
)