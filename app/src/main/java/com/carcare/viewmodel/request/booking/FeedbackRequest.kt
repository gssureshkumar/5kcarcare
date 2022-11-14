package com.carcare.viewmodel.request.booking

object FeedbackRequest {

    data class Request(
        val rating: Float,
        val review: String,
        val serviceIds: List<Int>,
        val outletId: Int,
        val bookingId: String
    )
}