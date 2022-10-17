package com.carcare.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.carcare.R


fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .centerCrop()
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_broken_image)
        .into(this)
}

fun ImageView.loadImageWithCorner(url: String) {
    val requestOptions =  RequestOptions().transforms(CenterCrop(), RoundedCorners(10))
    Glide.with(this)
        .load(url)
        .centerCrop()
        .apply(requestOptions)
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_broken_image)
        .into(this)
}