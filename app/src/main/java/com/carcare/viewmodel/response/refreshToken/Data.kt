package com.carcare.viewmodel.response.refreshToken

import com.google.gson.annotations.SerializedName

data class Data (

	@SerializedName("accessToken") val accessToken : String
)