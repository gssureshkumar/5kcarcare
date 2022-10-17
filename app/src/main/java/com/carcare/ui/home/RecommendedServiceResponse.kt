package com.carcare.ui.home

import com.carcare.viewmodel.response.services.DashboardResponse

object RecommendedServiceResponse {

     var dashboardResponse: DashboardResponse?=null

    var getServiceResponse: DashboardResponse
        get() = dashboardResponse!!
        set(value) {
            dashboardResponse = value
        }

}