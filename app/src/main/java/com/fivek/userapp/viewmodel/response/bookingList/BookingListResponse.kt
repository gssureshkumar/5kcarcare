package com.fivek.userapp.viewmodel.response.bookingList

import com.google.gson.annotations.SerializedName


data class BookingListResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Data
)