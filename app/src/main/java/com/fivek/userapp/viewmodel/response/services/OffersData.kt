package com.fivek.userapp.viewmodel.response.services

import com.google.gson.annotations.SerializedName

data class OffersData (
    @SerializedName("offers") val offers : MutableList<Offers>,
    @SerializedName("trending") val trending : MutableList<ServiceData>,
    @SerializedName("recommended") val recommended : MutableList<ServiceData>,
    @SerializedName("others") val others : MutableList<ServiceData>
)