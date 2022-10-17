package com.carcare.ui.checkout

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.carcare.network.RetrofitInstance
import com.carcare.utils.ErrorResponseParser
import com.carcare.viewmodel.BaseViewModel
import com.carcare.viewmodel.response.outlets.OutletsResponse
import com.carcare.viewmodel.response.timeslot.TimeSlotsResponse
import com.carcare.viewmodel.response.vouchers.VouchersResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CheckOutViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val outletsResponse = MutableLiveData<OutletsResponse>()
    val timeSlotsResponse = MutableLiveData<TimeSlotsResponse>()
    val vouchersResponse = MutableLiveData<VouchersResponse>()
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}