package com.carcare.utils

import android.content.Context
import com.carcare.R
import com.carcare.viewmodel.response.TutorialData

object TutorialDataManager {
    var tutorialList: MutableList<TutorialData> = mutableListOf()

    fun init(context: Context){
        tutorialList.add(TutorialData(0, R.drawable.car_wash, context.getString(R.string.car_polishing), context.getString(R.string.car_polishing_text)));
        tutorialList.add(TutorialData(1, R.drawable.car_wash, context.getString(R.string.car_polishing), context.getString(R.string.car_polishing_text)));
        tutorialList.add(TutorialData(2, R.drawable.car_wash, context.getString(R.string.car_polishing), context.getString(R.string.car_polishing_text)));
    }

    fun getTutorialData(): MutableList<TutorialData> {
        return tutorialList
    }

    fun getTutorialData(pos:Int): TutorialData {
        return tutorialList[pos]
    }


    fun getBannerImage(): MutableList<String> {
        var  bannerList = mutableListOf<String>()
        bannerList.add("https://cube.getpitstop.com/assets/img/all_cities_body_repair.png")
        bannerList.add("https://cube.getpitstop.com/assets/img/all_cities_body_repair.png")
        bannerList.add("https://cube.getpitstop.com/assets/img/all_cities_body_repair.png")

        return bannerList
    }

}