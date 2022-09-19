package com.carcare.ui.authentication

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.carcare.network.RetrofitInstance
import com.carcare.viewmodel.BaseViewModel
import com.carcare.viewmodel.request.LoginRequestBodies
import com.carcare.viewmodel.response.SuccessResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class AuthenticationViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val getOTPResponse = MutableLiveData<SuccessResponse>()
    val loginResponse = MutableLiveData<SuccessResponse>()
    val updateUserNameResponse = MutableLiveData<SuccessResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()


    fun getOTP(body: LoginRequestBodies.GetOTPBody) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.getOTP(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SuccessResponse>() {
                    override fun onSuccess(response: SuccessResponse) {
                        getOTPResponse.value = response
                        errorMessage.value = null
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = e.message
                        e.printStackTrace()
                    }
                })
        )
    }


    fun doLogin(body: LoginRequestBodies.LoginRequestBody) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.doLogin(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SuccessResponse>() {
                    override fun onSuccess(response: SuccessResponse) {
                        loginResponse.value = response
                        errorMessage.value = null
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = e.message
                        e.printStackTrace()
                    }
                })
        )
    }

    fun updateUserName(body: LoginRequestBodies.UpdateNameBody) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.updateUserName(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SuccessResponse>() {
                    override fun onSuccess(response: SuccessResponse) {
                        updateUserNameResponse.value = response
                        errorMessage.value = null
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = e.message
                        e.printStackTrace()
                    }
                })
        )
    }


    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }

}