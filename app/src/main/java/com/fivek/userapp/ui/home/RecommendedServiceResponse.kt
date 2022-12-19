package com.fivek.userapp.ui.home

import com.fivek.userapp.viewmodel.response.services.DashboardResponse

object RecommendedServiceResponse {

     var dashboardResponse: DashboardResponse?=null

    var getServiceResponse: DashboardResponse
        get() = dashboardResponse!!
        set(value) {
            dashboardResponse = value
        }

}