package com.carcare.network

import com.carcare.viewmodel.request.LoginRequestBodies
import com.carcare.viewmodel.request.booking.AddBookingRequest
import com.carcare.viewmodel.request.booking.FeedbackRequest
import com.carcare.viewmodel.request.vehicle.VehicleRequest
import com.carcare.viewmodel.response.GeneralResponse
import com.carcare.viewmodel.response.addBookingResponse.AddBookingResponse
import com.carcare.viewmodel.response.addVehicleResponse.AddVehicleResponse
import com.carcare.viewmodel.response.authendication.LoginResponse
import com.carcare.viewmodel.response.authendication.userType.UserTypeResponse
import com.carcare.viewmodel.response.bookingDetails.BookingDetailsResponse
import com.carcare.viewmodel.response.bookingList.BookingListResponse
import com.carcare.viewmodel.response.outlets.OutletsResponse
import com.carcare.viewmodel.response.refreshToken.RefreshTokenResponse
import com.carcare.viewmodel.response.services.DashboardResponse
import com.carcare.viewmodel.response.servicesDetailsResponse.ServiceDetailsResponse
import com.carcare.viewmodel.response.signedUrlResponse.SignedUrlResponse
import com.carcare.viewmodel.response.timeslot.TimeSlotsResponse
import com.carcare.viewmodel.response.vouchers.VouchersResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface API {

    @POST("user/getOTP")
    fun getOTP(@Body body: LoginRequestBodies.GetOTPBody): Single<GeneralResponse>

    @POST("user/login")
    fun doLogin(@Body body: LoginRequestBodies.LoginRequestBody): Single<LoginResponse>

    @POST("user/updateName")
    fun updateUserName(@Body body: LoginRequestBodies.UpdateNameBody): Single<GeneralResponse>

    @PUT("user/updateProfile")
    fun updateProfile(@Body body: LoginRequestBodies.UpdateProfileBody): Single<GeneralResponse>

    @POST("vehicle")
    fun addVehicleRequest(@Body body: VehicleRequest.AddVehicleRequest): Single<AddVehicleResponse>

    @HTTP(method = "DELETE", path = "vehicle", hasBody = true)
    fun deleteVehicleRequest(@Body body: VehicleRequest.DeleteVehicleRequest): Single<GeneralResponse>

    @GET("services")
    fun fetchServices(): Single<DashboardResponse>

    @GET("service")
    fun fetchServiceDetails(@QueryMap options: HashMap<String, Any>): Single<ServiceDetailsResponse>

    @GET("outlet")
    fun fetchOutlets(@QueryMap options: HashMap<String, Any>): Single<OutletsResponse>

    @GET("booking/timeslots")
    fun fetchTimeSlots(@QueryMap options: HashMap<String, Any>): Single<TimeSlotsResponse>

    @GET("user/vouchers")
    fun fetchVouchers(): Single<VouchersResponse>

    @POST("booking")
    fun addBookingRequest(@Body body: AddBookingRequest.BookingRequest): Single<AddBookingResponse>

    @POST("paymentStatus")
    fun paymentStatusRequest(@Body body: AddBookingRequest.PaymentStatusRequest): Single<GeneralResponse>

    @GET("bookings")
    fun getBookingList(): Single<BookingListResponse>

    @PUT("primaryVehicle")
    fun primaryVehicle(@Body body: VehicleRequest.primaryVehicle): Single<GeneralResponse>

    @GET("booking")
    fun fetchBookingDetails(@QueryMap options: HashMap<String, Any>): Single<BookingDetailsResponse>

    @PUT("feedback")
    fun updateFeedBack(@Body body: FeedbackRequest.Request): Single<GeneralResponse>

    @HTTP(method = "DELETE", path = "booking", hasBody = true)
    fun cancelBookingRequest(@Body body: AddBookingRequest.CancelBooking): Single<GeneralResponse>

    @POST("user/refreshToken")
    fun refreshToken(@Header("Authorization") authorization: String?, @Body body: LoginRequestBodies.RefreshTokenBody): Call<RefreshTokenResponse>

    @GET("user/userType")
    fun fetchUserType(): Single<UserTypeResponse>

    @POST("user/logout")
    fun userLogout(): Single<GeneralResponse>

    @GET("media/url")
    fun getSignedUrl(@QueryMap options: HashMap<String, Any>): Single<SignedUrlResponse>

}