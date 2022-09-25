package com.carcare.app

import android.app.Application
import com.carcare.database.VehicleDatabase
import com.carcare.database.VehicleRepository
import com.carcare.utils.PreferenceHelper
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CarCareApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { VehicleDatabase.getInstance(this) }
    val repository by lazy { VehicleRepository(database.vehicleDao()) }
    companion object {
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