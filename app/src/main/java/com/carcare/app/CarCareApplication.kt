package com.carcare.app

import android.app.Application
import com.carcare.database.CareDatabase
import com.carcare.database.CartRepository
import com.carcare.database.VehicleRepository
import com.carcare.utils.PreferenceHelper
import com.carcare.viewmodel.response.LocationInfoData
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