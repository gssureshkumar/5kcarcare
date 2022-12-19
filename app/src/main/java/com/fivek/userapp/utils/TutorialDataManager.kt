package com.fivek.userapp.utils

import android.content.Context
import com.fivek.userapp.R
import com.fivek.userapp.viewmodel.response.TutorialData
import com.fivek.userapp.viewmodel.response.services.Offers

object TutorialDataManager {
    var tutorialList: MutableList<TutorialData> = mutableListOf()

    var offersList: MutableList<Offers> = mutableListOf()

    fun init(context: Context) {
        tutorialList.add(TutorialData(0, R.drawable.ic_walk_through_2_icon, context.getString(R.string.car_wash), context.getString(R.string.car_wash_description)));
        tutorialList.add(TutorialData(1, R.drawable.ic_walk_through_1_icon, context.getString(R.string.car_polishing), context.getString(R.string.car_polishing_text)));
        tutorialList.add(TutorialData(2, R.drawable.ic_walk_through_4_icon, context.getString(R.string.car_accessories), context.getString(R.string.car_accessories_text)));
        tutorialList.add(TutorialData(2, R.drawable.ic_walk_through_3_icon, context.getString(R.string.car_maintance), context.getString(R.string.car_maintance_text)));
    }

    fun getTutorialData(): MutableList<TutorialData> {
        return tutorialList
    }

    fun getTutorialData(pos: Int): TutorialData {
        return tutorialList[pos]
    }


    var getOffersList: MutableList<Offers>
        get() = offersList
        set(value) {
            offersList = value
        }




}