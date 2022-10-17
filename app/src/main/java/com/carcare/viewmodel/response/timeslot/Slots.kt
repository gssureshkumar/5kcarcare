package com.carcare.viewmodel.response.timeslot

import com.google.gson.annotations.SerializedName

data class Slots (
	@SerializedName("id") val id : Int,
	@SerializedName("duration") val duration : String,
	@SerializedName("selected") var selected : Boolean
)