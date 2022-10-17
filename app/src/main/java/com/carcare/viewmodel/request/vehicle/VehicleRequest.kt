package com.carcare.viewmodel.request.vehicle

object VehicleRequest {

    data class AddVehicleRequest(val model: String, val type: String, val regNo: String, val primary: Boolean, val thumbnail: String)

    data class DeleteVehicleRequest(val id: String)
}