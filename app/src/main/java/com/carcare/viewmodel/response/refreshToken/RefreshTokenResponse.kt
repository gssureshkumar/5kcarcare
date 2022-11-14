package com.carcare.viewmodel.response.refreshToken

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse (
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : Data
)