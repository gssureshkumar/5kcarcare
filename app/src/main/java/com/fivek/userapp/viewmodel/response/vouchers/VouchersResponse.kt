package com.fivek.userapp.viewmodel.response.vouchers

import com.google.gson.annotations.SerializedName
data class VouchersResponse (

	@SerializedName("message") val message : String,
	@SerializedName("data") val vouches : List<Voucher>
)