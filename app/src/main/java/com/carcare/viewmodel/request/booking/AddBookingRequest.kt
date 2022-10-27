package com.carcare.viewmodel.request.booking

object AddBookingRequest {

    data class BookingRequest(
        val serviceIds: MutableList<Int>,
        val outletId: Int,
        val slotId: Int,
        val paymentMethod: String,
        val bookingDate: String,
        val vehicleId: String,
        val pickupRequired: Boolean,
        var voucherCode: String?,
        val lat: Double,
        val long: Double,
        val address: String
    )


    data class PaymentStatusRequest(
        val bookingId: String,
        val paymentId: String,
        val status: String
    )
}