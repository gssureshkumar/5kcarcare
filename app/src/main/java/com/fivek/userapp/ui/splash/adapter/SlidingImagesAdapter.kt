package com.fivek.userapp.ui.splash.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fivek.userapp.ui.splash.SlidingImageFragment

class SlidingImagesAdapter(activity: AppCompatActivity, private val itemsCount: Int) :
    FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) = SlidingImageFragment.getInstance(position)
}