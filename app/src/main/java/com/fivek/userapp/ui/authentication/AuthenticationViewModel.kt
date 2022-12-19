package com.fivek.userapp.ui.authentication

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fivek.userapp.network.RetrofitInstance
import com.fivek.userapp.utils.ErrorResponseParser
import com.fivek.userapp.viewmodel.BaseViewModel
import com.fivek.userapp.viewmodel.request.LoginRequestBodies
import com.fivek.userapp.viewmodel.response.GeneralResponse
import com.fivek.userapp.viewmodel.response.authendication.LoginResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class AuthenticationViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val getOTPResponse = MutableLiveData<GeneralResponse>()
    val loginResponse = MutableLiveData<LoginResponse>()
    val updateUserNameResponse = MutableLiveData<GeneralResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()


    fun getOTP(body: LoginRequestBodies.GetOTPBody) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.getOTP(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        getOTPResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

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
                .subscribeWith(object : DisposableSingleObserver<LoginResponse>() {
                    override fun onSuccess(response: LoginResponse) {
                        loginResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)
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
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        updateUserNameResponse.value = response
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