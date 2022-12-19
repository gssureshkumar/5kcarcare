package com.fivek.userapp.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fivek.userapp.network.RetrofitInstance
import com.fivek.userapp.utils.ErrorResponseParser
import com.fivek.userapp.viewmodel.BaseViewModel
import com.fivek.userapp.viewmodel.response.GeneralResponse
import com.fivek.userapp.viewmodel.response.authendication.userType.UserTypeResponse
import com.fivek.userapp.viewmodel.response.services.DashboardResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val dashBoardResponse = MutableLiveData<DashboardResponse>()
    val userTypeResponse = MutableLiveData<UserTypeResponse>()
    val userLogoutResponse = MutableLiveData<GeneralResponse>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()


    fun fetchDashBoardResponse(options: HashMap<String, Any>) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchServices(options)
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

    fun fetchUserType() {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.fetchUserType()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UserTypeResponse>() {
                    override fun onSuccess(response: UserTypeResponse) {
                        userTypeResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    fun userLogout() {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.userLogout()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        userLogoutResponse.value = response
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }


}