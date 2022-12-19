package com.fivek.userapp.ui.bookingList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fivek.userapp.network.RetrofitInstance
import com.fivek.userapp.utils.ErrorResponseParser
import com.fivek.userapp.viewmodel.BaseViewModel
import com.fivek.userapp.viewmodel.response.bookingList.BookingListResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class BookingViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val bookingListResponse = MutableLiveData<BookingListResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun getBookingList() {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.getBookingList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<BookingListResponse>() {
                    override fun onSuccess(response: BookingListResponse) {
                        bookingListResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

}