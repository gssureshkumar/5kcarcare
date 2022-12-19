package com.fivek.userapp.utils

import android.util.Log
import com.fivek.userapp.BuildConfig

object Logger {

    fun logDebug(tag:String,mess:String){
        if (BuildConfig.DEBUG){
            Log.e(tag,mess)
        }
    }

    fun logInfo(tag:String,mess:String){
        if (BuildConfig.DEBUG){
            Log.v(tag,mess)
        }
    }
}