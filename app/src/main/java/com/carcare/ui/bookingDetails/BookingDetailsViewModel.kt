package com.carcare.ui.bookingDetails

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.carcare.network.RetrofitInstance
import com.carcare.utils.ErrorResponseParser
import com.carcare.viewmodel.BaseViewModel
import com.carcare.viewmodel.request.booking.AddBookingRequest
import com.carcare.viewmodel.request.booking.FeedbackRequest
import com.carcare.viewmodel.response.GeneralResponse
import com.carcare.viewmodel.response.bookingDetails.BookingDetailsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class BookingDetailsViewModel(application: Application) : BaseViewModel(application) {
    private val disposable = CompositeDisposable()

    val bookingDetailsResponse = MutableLiveData<BookingDetailsResponse>()
    val feedbackResponse = MutableLiveData<GeneralResponse>()
    val invoiceResponse = MutableLiveData<Boolean>()
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

    fun downloadInvoiceResponse(query: String, file:File) {
        isLoading.value = true
        Log.e("absolutePath -->", "downloadInvoiceResponse: "+file.absolutePath )
        RetrofitInstance.api.downloadInvoice(query)!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    invoiceResponse.value =  response.body()?.let { writeResponseBodyToDisk(it, file) }
                    isLoading.value = false
                } else {
                    isLoading.value = false
                    errorMessage.value = ErrorResponseParser.getErrorResponse(Exception("Invalid file!!"))
                }
            }

            override fun onFailure(call: Call<ResponseBody?>?, e: Throwable) {
                isLoading.value = false
                errorMessage.value = ErrorResponseParser.getErrorResponse(e)
            }
        })

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

    private fun writeResponseBodyToDisk(body: ResponseBody, file:File): Boolean {
        return try {
            // todo change the file location/name according to your needs
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream!!.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()

                }
                outputStream!!.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            false
        }
    }
}