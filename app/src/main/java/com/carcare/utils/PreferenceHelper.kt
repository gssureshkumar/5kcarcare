package com.carcare.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val SHARED_PREF_NAME = "shared_pref_name"
    private val USER_ID = "user_id"
    private val USER_NAME = "user_name"
    private val MOBILE_NUMBER = "mobile_number"
    private val AUTH_TOKEN = "auth_token"
    private val TUTORIAL_UPDATED = "tutorial_updated"
    private val REF_CODE = "ref_code"
    private val DEVICE_TOKEN = "device_token"


    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    var intUserIDPref: String?
        get() = preferences.getString(USER_ID, "")
        set(value) = preferences.edit().putString(USER_ID, value).apply()

    var intUserNamePref: String?
        get() = preferences.getString(USER_NAME, "")
        set(value) = preferences.edit().putString(USER_NAME, value).apply()

    var intTutorialPref: Boolean
        get() = preferences.getBoolean(TUTORIAL_UPDATED, false)
        set(value) = preferences.edit().putBoolean(TUTORIAL_UPDATED, value).apply()

    var intAuthTokenPref: String?
        get() = preferences.getString(AUTH_TOKEN, "")
        set(value) = preferences.edit().putString(AUTH_TOKEN, value).apply()

    var intRefCodePref: String?
        get() = preferences.getString(REF_CODE, "")
        set(value) = preferences.edit().putString(REF_CODE, value).apply()

    var intDeviceTokenPref: String?
        get() = preferences.getString(DEVICE_TOKEN, "")
        set(value) = preferences.edit().putString(DEVICE_TOKEN, value).apply()


    var intMobileNumberPref: String?
        get() = preferences.getString(MOBILE_NUMBER, "")
        set(value) = preferences.edit().putString(MOBILE_NUMBER, value).apply()



    fun logout(){
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}