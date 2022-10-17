package com.carcare.network

import com.carcare.viewmodel.request.LoginRequestBodies
import com.carcare.viewmodel.request.vehicle.VehicleRequest
import com.carcare.viewmodel.response.GeneralResponse
import com.carcare.viewmodel.response.addVehicleResponse.AddVehicleResponse
import com.carcare.viewmodel.response.authendication.LoginResponse
import com.carcare.viewmodel.response.outlets.OutletsResponse
import com.carcare.viewmodel.response.services.DashboardResponse
import com.carcare.viewmodel.response.servicesDetailsResponse.ServiceDetailsResponse
import com.carcare.viewmodel.response.timeslot.TimeSlotsResponse
import com.carcare.viewmodel.response.vouchers.VouchersResponse
import io.reactivex.Single
import retrofit2.http.*

interface API {

    @POST("user/getOTP")
    fun getOTP (@Body body: LoginRequestBodies.GetOTPBody): Single<GeneralResponse>

    @POST("user/login")
    fun doLogin (@Body body: LoginRequestBodies.LoginRequestBody): Single<LoginResponse>

    @POST("user/updateName")
    fun updateUserName (@Body body: LoginRequestBodies.UpdateNameBody): Single<GeneralResponse>

    @PUT("user/updateProfile")
    fun updateProfile(@Body body: LoginRequestBodies.UpdateProfileBody): Single<GeneralResponse>

    @POST("vehicle")
    fun addVehicleRequest(@Body body: VehicleRequest.AddVehicleRequest): Single<AddVehicleResponse>

    @HTTP(method = "DELETE", path = "vehicle", hasBody = true)
    fun deleteVehicleRequest(@Body body: VehicleRequest.DeleteVehicleRequest): Single<GeneralResponse>

    @GET("services")
    fun fetchServices(): Single<DashboardResponse>

    @GET("service")
    fun fetchServiceDetails(@QueryMap options:HashMap<String, Any>): Single<ServiceDetailsResponse>

    @GET("outlet")
    fun fetchOutlets(@QueryMap options:HashMap<String, Any>): Single<OutletsResponse>

    @GET("booking/timeslots")
    fun fetchTimeSlots(@QueryMap options:HashMap<String, Any>): Single<TimeSlotsResponse>

    @GET("user/vouchers")
    fun fetchVouchers(): Single<VouchersResponse>


}