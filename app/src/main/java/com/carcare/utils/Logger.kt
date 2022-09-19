package com.carcare.utils

import android.util.Log
import com.carcare.BuildConfig

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