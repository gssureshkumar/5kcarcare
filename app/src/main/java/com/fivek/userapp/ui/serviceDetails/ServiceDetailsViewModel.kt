package com.fivek.userapp.ui.serviceDetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fivek.userapp.network.RetrofitInstance
import com.fivek.userapp.utils.ErrorResponseParser
import com.fivek.userapp.viewmodel.BaseViewModel
import com.fivek.userapp.viewmodel.response.servicesDetailsResponse.ServiceDetailsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ServiceDetailsViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val serviceDetailsResponse = MutableLiveData<ServiceDetailsResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchDetailsResponse(query: HashMap<String, Any>) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchServiceDetails(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ServiceDetailsResponse>() {
                    override fun onSuccess(response: ServiceDetailsResponse) {
                        serviceDetailsResponse.value = response
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