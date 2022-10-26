package com.carcare.viewmodel.response.addBookingResponse

import com.google.gson.annotations.SerializedName

data class Data (

	@SerializedName("serviceIds") val serviceIds : List<Int>,
	@SerializedName("outletId") val outletId : Int,
	@SerializedName("slotId") val slotId : Int,
	@SerializedName("paymentMethod") val paymentMethod : String,
	@SerializedName("bookingDate") val bookingDate : String,
	@SerializedName("vehicleId") val vehicleId : String,
	@SerializedName("voucherCode") val voucherCode : String,
	@SerializedName("lat") val lat : Double,
	@SerializedName("long") val long : Double,
	@SerializedName("address") val address : String,
	@SerializedName("userId") val userId : String,
	@SerializedName("actual") val actual : Double,
	@SerializedName("offer") val offer : Double,
	@SerializedName("membership") val membership : Double,
	@SerializedName("voucher") val voucher : Double,
	@SerializedName("id") val id : String,
	@SerializedName("paymentStatus") val paymentStatus : String,
	@SerializedName("status") val status : String,
	@SerializedName("createdAt") val createdAt : String
)