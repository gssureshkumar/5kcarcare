package com.carcare.utils

import com.carcare.viewmodel.response.ErrorResponse
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
                    return errorResponse.message
                }
            }
        }
        return error.message
    }
}