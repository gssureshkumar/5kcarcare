package com.fivek.userapp.ui.home.banner

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerImagesAdapter(activity: AppCompatActivity, private val itemsCount: Int, private val bannerCallBack: BannerCallBack) :
    FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) =
        BannerImageFragment.getInstance(position, bannerCallBack)
}


interface BannerCallBack {
    fun bannerClick(id: Int?)
}