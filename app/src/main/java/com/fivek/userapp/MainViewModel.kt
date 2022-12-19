package com.fivek.userapp

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fivek.userapp.network.RetrofitInstance
import com.fivek.userapp.utils.ErrorResponseParser
import com.fivek.userapp.viewmodel.BaseViewModel
import com.fivek.userapp.viewmodel.request.LoginRequestBodies
import com.fivek.userapp.viewmodel.request.vehicle.VehicleRequest
import com.fivek.userapp.viewmodel.response.GeneralResponse
import com.fivek.userapp.viewmodel.response.addVehicleResponse.AddVehicleResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel (application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val addVehicleResponse = MutableLiveData<AddVehicleResponse>()
    val updatePrimaryVehicleResponse = MutableLiveData<GeneralResponse>()
    val deleteVehicleResponse = MutableLiveData<GeneralResponse>()
    val updateUserNameResponse = MutableLiveData<GeneralResponse>()
    val updateProfileResponse = MutableLiveData<GeneralResponse>()
    val errorMessage = MutableLiveData<String?>()
    val errorUpdateMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()


    fun addVehicleRequest(body: VehicleRequest.AddVehicleRequest) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.addVehicleRequest(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<AddVehicleResponse>() {
                    override fun onSuccess(response: AddVehicleResponse) {
                        addVehicleResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorUpdateMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }


    fun deleteVehicleRequest(body: VehicleRequest.DeleteVehicleRequest) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.deleteVehicleRequest(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        deleteVehicleResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorUpdateMessage.value = ErrorResponseParser.getErrorResponse(e)

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


    fun updateProfile(body: LoginRequestBodies.UpdateProfileBody) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.updateProfile(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        updateProfileResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorMessage.value = ErrorResponseParser.getErrorResponse(e)
                    }
                })
        )
    }

    fun updatePrimaryVehicle(body: VehicleRequest.primaryVehicle) {
        isLoading.value = true
        disposable.add(
            RetrofitInstance.api.primaryVehicle(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GeneralResponse>() {
                    override fun onSuccess(response: GeneralResponse) {
                        updatePrimaryVehicleResponse.value = response
                        isLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        errorUpdateMessage.value = ErrorResponseParser.getErrorResponse(e)

                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}