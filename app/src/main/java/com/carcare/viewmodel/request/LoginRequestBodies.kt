package com.carcare.viewmodel.request

object LoginRequestBodies {

    data class GetOTPBody(
        val mobileNumber: String
    )

    data class LoginRequestBody(
        val mobileNumber: String,
        val otp: String
    )

    data class UpdateNameBody(
        val id: String,
        val name: String
    )


    data class UpdateProfileBody(
        val lat: Double,
        val long: Double,
        val address: String,
        val city: String,
        val state: String,
        val deviceType: String,
        val token: String,
    )
}