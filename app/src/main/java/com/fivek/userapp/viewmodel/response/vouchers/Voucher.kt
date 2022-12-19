package com.fivek.userapp.viewmodel.response.vouchers

import com.google.gson.annotations.SerializedName

data class Voucher (
	@SerializedName("id") val id : String,
	@SerializedName("userId") val userId : String,
	@SerializedName("type") val type : String,
	@SerializedName("value") val value : Int
)