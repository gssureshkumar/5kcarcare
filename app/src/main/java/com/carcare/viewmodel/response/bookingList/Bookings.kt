package com.carcare.viewmodel.response.bookingList

import com.google.gson.annotations.SerializedName

data class Bookings (

	@SerializedName("id") val id : String,
	@SerializedName("userId") val userId : String,
	@SerializedName("paymentMethod") val paymentMethod : String,
	@SerializedName("paymentStatus") val paymentStatus : String,
	@SerializedName("status") val status : String,
	@SerializedName("membership") val membership : Int,
	@SerializedName("voucherCode") val voucherCode : String,
	@SerializedName("slotId") val slotId : Int,
	@SerializedName("outletId") val outletId : Int,
	@SerializedName("actual") val actual : Double,
	@SerializedName("offer") val offer : Double,
	@SerializedName("voucher") val voucher : Double,
	@SerializedName("bookingDate") val bookingDate : String,
	@SerializedName("lat") val lat : Double,
	@SerializedName("long") val long : Double,
	@SerializedName("address") val address : String,
	@SerializedName("createdAt") val createdAt : String,
	@SerializedName("paymentId") val paymentId : String,
	@SerializedName("pickupRequired") val pickupRequired : Boolean,
	@SerializedName("finalPrice") val finalPrice : Double,
	@SerializedName("serviceIds") val serviceIds : List<Int>,
	@SerializedName("mobileNumber") val mobileNumber : Long,
	@SerializedName("name") val name : String,
	@SerializedName("rating") val rating : Int,
	@SerializedName("elite") val elite : Boolean,
	@SerializedName("city") val city : String,
	@SerializedName("state") val state : String,
	@SerializedName("tier") val tier : Int
)