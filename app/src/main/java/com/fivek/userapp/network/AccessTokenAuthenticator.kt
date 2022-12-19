package com.fivek.userapp.network

import com.fivek.userapp.app.CarCareApplication
import com.fivek.userapp.utils.PreferenceHelper
import com.fivek.userapp.viewmodel.request.LoginRequestBodies
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AccessTokenAuthenticator() : Authenticator {

    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }


    override fun authenticate(route: Route?, response: Response): Request? {
        val updatedToken = RetrofitInstance.refreshAPI.refreshToken("Bearer "+prefsHelper.intRefreshTokenPref, LoginRequestBodies.RefreshTokenBody(prefsHelper.intUserIDPref!!)).execute()
        if (updatedToken.body() != null) {
            prefsHelper.intAuthTokenPref = updatedToken.body()!!.data.accessToken
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${prefsHelper.intAuthTokenPref}")
                .build()
        }
        return null
    }
}

