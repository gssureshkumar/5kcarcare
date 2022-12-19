package com.fivek.userapp.utils

import com.fivek.userapp.viewmodel.response.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

object ErrorResponseParser {

    fun getErrorResponse(error: Throwable): String? {
        error.printStackTrace()
        if(error is HttpException) {
            val httpException: HttpException = error
            if (httpException.response() != null && httpException.response()!!.errorBody() != null) {
                val errorBody: String = httpException.response()?.errorBody()!!.string()
                val errorResponse: ErrorResponse =
                    Gson().fromJson(errorBody, ErrorResponse::class.java)
                if (errorResponse.statusCode == 400) {
                    if((errorResponse.message is String)) {
                        return errorResponse.message
                    }else if((errorResponse.message is Array<*>)) {
                        return errorResponse.message[0].toString()
                    }
                }
            }
        }
        return error.message
    }
}