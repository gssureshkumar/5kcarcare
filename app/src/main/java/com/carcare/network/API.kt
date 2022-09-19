package com.carcare.network

import com.carcare.viewmodel.Country
import com.carcare.viewmodel.request.LoginRequestBodies
import com.carcare.viewmodel.response.SuccessResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface API {

    @POST("user/getOTP")
    fun getOTP (@Body body: LoginRequestBodies.GetOTPBody): Single<SuccessResponse>

    @POST("user/login")
    fun doLogin (@Body body: LoginRequestBodies.LoginRequestBody): Single<SuccessResponse>

    @POST("user/updateProfile")
    fun updateUserName (@Body body: LoginRequestBodies.UpdateNameBody): Single<SuccessResponse>
}