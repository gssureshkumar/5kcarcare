package com.carcare.viewmodel.response.signedUrlResponse

import com.google.gson.annotations.SerializedName

data class SignedUrlResponse (
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : Data
)