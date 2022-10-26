package com.carcare.ui.bookingList

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carcare.network.RetrofitInstance
import com.carcare.utils.ErrorResponseParser
import com.carcare.viewmodel.BaseViewModel
import com.carcare.viewmodel.request.vehicle.VehicleRequest
import com.carcare.viewmodel.response.GeneralResponse
import com.carcare.viewmodel.response.addVehicleResponse.AddVehicleResponse
import com.carcare.viewmodel.response.bookingList.BookingListResponse
import com.carcare.viewmodel.response.services.DashboardResponse
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