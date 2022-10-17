package com.carcare.viewmodel.response.services

import com.google.gson.annotations.SerializedName


data class DashboardResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val data : OffersData
)