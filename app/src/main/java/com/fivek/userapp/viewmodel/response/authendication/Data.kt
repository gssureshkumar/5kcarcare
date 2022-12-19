package com.fivek.userapp.viewmodel.response.authendication

import com.google.gson.annotations.SerializedName

data class Data (

	@SerializedName("id") val id : String,
	@SerializedName("otp") val otp : Int,
	@SerializedName("name") val name : String,
	@SerializedName("refCode") val refCode : String,
	@SerializedName("updated_at") val updated_at : String,
	@SerializedName("accessToken") val accessToken : String,
	@SerializedName("refreshToken") val refreshToken : String,
	@SerializedName("type") val type : String,
	@SerializedName("vehicles") val vehicles : List<Vehicles>
)