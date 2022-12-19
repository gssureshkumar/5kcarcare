package com.fivek.userapp.viewmodel.response.timeslot

import com.google.gson.annotations.SerializedName

data class TimeSlotsResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Data
)