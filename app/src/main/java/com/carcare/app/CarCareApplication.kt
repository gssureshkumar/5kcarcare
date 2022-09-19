package com.carcare.app

import android.app.Application
import com.carcare.utils.PreferenceHelper

class CarCareApplication : Application() {


    companion object {
        var prefs: PreferenceHelper? = null
        lateinit var instance: CarCareApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = PreferenceHelper(applicationContext)
    }
}