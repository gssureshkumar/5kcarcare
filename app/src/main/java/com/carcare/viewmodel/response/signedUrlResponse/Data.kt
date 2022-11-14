package com.carcare.viewmodel.response.signedUrlResponse

import com.google.gson.annotations.SerializedName

data class Data (
	@SerializedName("url") val url : String,
	@SerializedName("objectId") val objectId : String
)