package com.fivek.userapp.viewmodel.response.outlets

import com.google.gson.annotations.SerializedName
data class OutletsResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val data : MutableList<Outlet>
)