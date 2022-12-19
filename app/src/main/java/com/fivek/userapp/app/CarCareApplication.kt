package com.fivek.userapp.app

import android.app.Application
import com.fivek.userapp.database.CareDatabase
import com.fivek.userapp.database.CartRepository
import com.fivek.userapp.database.VehicleRepository
import com.fivek.userapp.utils.PreferenceHelper
import com.fivek.userapp.viewmodel.response.LocationInfoData
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CarCareApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    val careDatabase by lazy { CareDatabase.getInstance(this) }
    val repository by lazy { VehicleRepository(careDatabase.vehicleDao()) }
    val cartRepository by lazy { CartRepository(careDatabase.cartDao()) }

     var location: LocationInfoData? = null
    var locationInfoData: LocationInfoData
        get() = location!!
        set(value) {
            location = value
        }


    companion

    object {
        var prefs: PreferenceHelper? = null

        lateinit var instance: CarCareApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = PreferenceHelper(applicationContext)
        FirebaseApp.initializeApp(this)
    }
}