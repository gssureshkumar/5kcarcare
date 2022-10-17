package com.carcare.viewmodel.response.servicesDetailsResponse

import com.google.gson.annotations.SerializedName

data class Data (

	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("duration") val duration : Int,
	@SerializedName("thumbnail") val thumbnail : String,
	@SerializedName("banner") val banner : String,
	@SerializedName("includes") val includes : List<String>,
	@SerializedName("rating") val rating : Int,
	@SerializedName("sedan") val sedan : Int,
	@SerializedName("suv") val suv : Int,
	@SerializedName("7Seater") val seater : Int,
	@SerializedName("actual") val actual : Long,
	@SerializedName("offer") val offer : Long,
	@SerializedName("reviews") val reviews : List<Reviews>
)