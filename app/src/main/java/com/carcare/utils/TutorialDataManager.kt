package com.carcare.utils

import android.content.Context
import com.carcare.R
import com.carcare.viewmodel.response.TutorialData

object TutorialDataManager {
    var tutorialList: MutableList<TutorialData> = mutableListOf()

    fun init(context: Context){
        tutorialList.add(TutorialData(0, R.drawable.ic_walk_through_1_icon, context.getString(R.string.car_wash), context.getString(R.string.car_wash_description)));
        tutorialList.add(TutorialData(1, R.drawable.ic_walk_through_2_icon, context.getString(R.string.car_polishing), context.getString(R.string.car_polishing_text)));
        tutorialList.add(TutorialData(2, R.drawable.ic_walk_through_3_icon, context.getString(R.string.car_accessories), context.getString(R.string.car_accessories_text)));
        tutorialList.add(TutorialData(2, R.drawable.ic_walk_through_4_icon, context.getString(R.string.car_maintance), context.getString(R.string.car_maintance_text)));
    }

    fun getTutorialData(): MutableList<TutorialData> {
        return tutorialList
    }

    fun getTutorialData(pos:Int): TutorialData {
        return tutorialList[pos]
    }


    fun getBannerImage(): MutableList<String> {
        val  bannerList = mutableListOf<String>()
        bannerList.add("https://img.freepik.com/free-vector/car-wash-advertising-banner_1409-962.jpg")
        bannerList.add("https://img.freepik.com/free-vector/car-wash-advertising-banner_1409-962.jpg")
        bannerList.add("https://img.freepik.com/free-vector/car-wash-advertising-banner_1409-962.jpg")
        return bannerList
    }

}