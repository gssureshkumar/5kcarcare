package com.carcare.viewmodel.response.bookingList

import com.google.gson.annotations.SerializedName

data class Data (

	@SerializedName("bookings") val bookings : List<Bookings>,
	@SerializedName("services") val services : MutableList<Services>
)