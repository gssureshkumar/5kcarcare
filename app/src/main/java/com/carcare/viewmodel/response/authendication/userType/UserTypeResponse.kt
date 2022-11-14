package com.carcare.viewmodel.response.authendication.userType

import com.google.gson.annotations.SerializedName

data class UserTypeResponse (
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : Data
)