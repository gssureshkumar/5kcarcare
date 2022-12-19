package com.fivek.userapp.database

import androidx.lifecycle.LiveData

class VehicleRepository(private val vehicleDao: VehicleDao) {

    val allVehicles: LiveData<List<VehicleModel>> = vehicleDao.getAllVehicles()

    val primaryVehicle: LiveData<VehicleModel> = vehicleDao.getPrimaryVehicle()

     fun insert(vehicle: VehicleModel) {
         if(allVehicles.value != null) {
             for (model in allVehicles.value!!) {
                 model.isPrimary = false
                 vehicleDao.insert(model)
             }
         }
         vehicle.isPrimary = true
        vehicleDao.insert(vehicle)
    }



    fun insert(vehicle: List<VehicleModel>) {
        vehicleDao.insert(vehicle)
    }

    fun  delete(vehicleId: String){
        vehicleDao.delete(vehicleId)
    }
}