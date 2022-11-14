package com.carcare.ui.bookingDetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.carcare.network.RetrofitInstance
import com.carcare.utils.ErrorResponseParser
import com.carcare.viewmodel.BaseViewModel
import com.carcare.viewmodel.request.booking.AddBookingRequest
import com.carcare.viewmodel.request.booking.FeedbackRequest
import com.carcare.viewmodel.response.GeneralResponse
import com.carcare.viewmodel.response.bookingDetails.BookingDetailsResponse
import com.carcare.viewmodel.response.servicesDetailsResponse.ServiceDetailsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class BookingDetailsViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val bookingDetailsResponse = MutableLiveData<BookingDetailsResponse>()
    val feedbackResponse = MutableLiveData<GeneralResponse>()
    val cancelBookingResponse = MutableLiveData<GeneralResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchBookingDetailsResponse(query: HashMap<String, Any>) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchBookingDetails(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<BookingDetailsResponse>() {
                    override fun onSuccess(response: BookingDetailsResponse) {
                        bookingDetailsResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    fun updateFeedback(query: FeedbackRequest.Request) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.updateFeedBack(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        feedbackResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    fun cancelBooking(query: AddBookingRequest.CancelBooking) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.cancelBookingRequest(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        cancelBookingResponse.value = response
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