package com.carcare.viewmodel.request

object LoginRequestBodies {

    data class GetOTPBody(
        val mobile:String
    )

    data class LoginRequestBody(
        val mobile:String,
        val otp:String
    )

    data class UpdateNameBody(
        val id:String,
        val name:String
    )
}