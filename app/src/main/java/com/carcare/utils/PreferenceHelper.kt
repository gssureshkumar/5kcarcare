package com.carcare.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val SHARED_PREF_NAME = "shared_pref_name"
    private val USER_ID = "user_id"
    private val USER_NAME = "user_name"
    private val TUTORIAL_UPDATED = "tutorial_updated"


    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)

    var intUserIDPref: String?
        get() = preferences.getString(USER_ID,"")
        set(value) = preferences.edit().putString(USER_ID, value).apply()

    var intUserNamePref: String?
        get() = preferences.getString(USER_NAME,"")
        set(value) = preferences.edit().putString(USER_NAME, value).apply()

    var intTutorialPref: Boolean
        get() = preferences.getBoolean(TUTORIAL_UPDATED, false)
        set(value) = preferences.edit().putBoolean(TUTORIAL_UPDATED, value).apply()
}