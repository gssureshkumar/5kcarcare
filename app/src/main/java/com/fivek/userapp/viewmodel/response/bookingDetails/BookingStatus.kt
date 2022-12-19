package com.fivek.userapp.viewmodel.response.bookingDetails

import com.google.gson.annotations.SerializedName

data class BookingStatus (

	@SerializedName("status") val status : String,
	@SerializedName("date") val date : String,
	@SerializedName("driverName") val name : String,
	@SerializedName("driverMobile") val mobile : String
)