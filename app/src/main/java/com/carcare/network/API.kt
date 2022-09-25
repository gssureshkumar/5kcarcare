package com.carcare.network

import com.carcare.viewmodel.request.LoginRequestBodies
import com.carcare.viewmodel.response.GeneralResponse
import com.carcare.viewmodel.response.authendication.LoginResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface API {

    @POST("user/getOTP")
    fun getOTP (@Body body: LoginRequestBodies.GetOTPBody): Single<GeneralResponse>

    @POST("user/login")
    fun doLogin (@Body body: LoginRequestBodies.LoginRequestBody): Single<LoginResponse>

    @PUT("user/updateProfile")
    fun updateUserName (@Body body: LoginRequestBodies.UpdateNameBody): Single<GeneralResponse>
}