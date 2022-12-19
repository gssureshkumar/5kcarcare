package com.fivek.userapp.viewmodel.response.authendication

import com.google.gson.annotations.SerializedName


data class LoginResponse (
	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Data
)