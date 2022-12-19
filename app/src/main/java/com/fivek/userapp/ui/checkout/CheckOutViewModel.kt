package com.fivek.userapp.ui.checkout

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fivek.userapp.network.RetrofitInstance
import com.fivek.userapp.utils.ErrorResponseParser
import com.fivek.userapp.viewmodel.BaseViewModel
import com.fivek.userapp.viewmodel.request.booking.AddBookingRequest
import com.fivek.userapp.viewmodel.response.GeneralResponse
import com.fivek.userapp.viewmodel.response.addBookingResponse.AddBookingResponse
import com.fivek.userapp.viewmodel.response.outlets.OutletsResponse
import com.fivek.userapp.viewmodel.response.signedUrlResponse.SignedUrlResponse
import com.fivek.userapp.viewmodel.response.timeslot.TimeSlotsResponse
import com.fivek.userapp.viewmodel.response.vouchers.VouchersResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CheckOutViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()
    val outletsResponse = MutableLiveData<OutletsResponse>()
    val timeSlotsResponse = MutableLiveData<TimeSlotsResponse>()
    val vouchersResponse = MutableLiveData<VouchersResponse>()
    val addBookingResponse = MutableLiveData<AddBookingResponse>()
    val paymentStatusResponse = MutableLiveData<GeneralResponse>()
    val signedUrlResponse = MutableLiveData<SignedUrlResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchOutLets(query: HashMap<String, Any>) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchOutlets(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<OutletsResponse>() {
                    override fun onSuccess(response: OutletsResponse) {
                        outletsResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    fun fetchTimeSlots(query: HashMap<String, Any>) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchTimeSlots(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<TimeSlotsResponse>() {
                    override fun onSuccess(response: TimeSlotsResponse) {
                        timeSlotsResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }


    fun fetchVouchers() {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchVouchers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<VouchersResponse>() {
                    override fun onSuccess(response: VouchersResponse) {
                        vouchersResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    fun addBookingRequest(body: AddBookingRequest.BookingRequest) {
        isLoading.value = true
        if(body.voucherCode != null && body.voucherCode!!.isEmpty()  ){
            body.voucherCode = null
        }
        disposable.add(
            RetrofitInstance.api.addBookingRequest(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<AddBookingResponse>() {
                    override fun onSuccess(response: AddBookingResponse) {
                        addBookingResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    fun paymentStatusRequest(body: AddBookingRequest.PaymentStatusRequest) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.paymentStatusRequest(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        paymentStatusResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    fun getSignedUrl(options: HashMap<String, Any>) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.getSignedUrl(options)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SignedUrlResponse>() {
                    override fun onSuccess(response: SignedUrlResponse) {
                        signedUrlResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}