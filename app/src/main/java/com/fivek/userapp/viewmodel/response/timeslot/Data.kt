package com.fivek.userapp.viewmodel.response.timeslot

import com.google.gson.annotations.SerializedName
data class Data (
	@SerializedName("slots") val slots : MutableList<Slots>
)