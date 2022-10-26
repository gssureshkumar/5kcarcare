package com.carcare.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.carcare.network.RetrofitInstance
import com.carcare.utils.ErrorResponseParser
import com.carcare.viewmodel.BaseViewModel
import com.carcare.viewmodel.request.vehicle.VehicleRequest
import com.carcare.viewmodel.response.GeneralResponse
import com.carcare.viewmodel.response.addVehicleResponse.AddVehicleResponse
import com.carcare.viewmodel.response.services.DashboardResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val dashBoardResponse = MutableLiveData<DashboardResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()


    fun fetchDashBoardResponse() {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchServices()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<DashboardResponse>() {
                    override fun onSuccess(response: DashboardResponse) {
                        dashBoardResponse.value = response
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