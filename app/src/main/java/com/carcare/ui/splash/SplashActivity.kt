package com.carcare.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.carcare.MainActivity
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.databinding.ActivitySplashBinding
import com.carcare.ui.BaseActivity
import com.carcare.ui.authentication.LoginActivity
import com.carcare.ui.splash.adapter.SlidingImagesAdapter
import com.carcare.utils.PreferenceHelper
import com.carcare.utils.TutorialDataManager
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity :BaseActivity() {
    private lateinit var slidingImageDots: Array<ImageView?>
    private var slidingDotsCount = 0
    private lateinit var binding: ActivitySplashBinding
    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }


    private val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {

            if(position == (slidingDotsCount-1)){
                binding.skipAction.text = getString(R.string.done)
            }
            for (i in 0 until slidingDotsCount) {
                slidingImageDots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@SplashActivity,
                        R.drawable.non_active_dot
                    )
                )
            }

            slidingImageDots[position]?.setImageDrawable(
                ContextCompat.getDrawable(
                   this@SplashActivity,
                    R.drawable.active_dot
                )
            )


        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TutorialDataManager.init(this@SplashActivity)
        setUpSlidingViewPager()

        binding.skipAction.setOnClickListener {
            prefsHelper.intTutorialPref = true
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.splashView.visibility = View.VISIBLE
        binding.promotionContainer.visibility = View.GONE
        Timer().schedule(2000) {
            runOnUiThread {
                if (prefsHelper.intTutorialPref) {
                    if (prefsHelper.intUserIDPref.isNullOrEmpty()) {
                        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    binding.splashView.visibility = View.GONE
                    binding.promotionContainer.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpSlidingViewPager() {
        slidingDotsCount = TutorialDataManager.getTutorialData().size
        val landingImagesAdapter =
            SlidingImagesAdapter(this, slidingDotsCount)
        binding.slidingViewPager.apply {
            adapter = landingImagesAdapter
            registerOnPageChangeCallback(slidingCallback)
        }

        slidingImageDots = arrayOfNulls(slidingDotsCount)

        binding.sliderDots.removeAllViews()
        for (i in 0 until slidingDotsCount) {
            slidingImageDots[i] = ImageView(this@SplashActivity)
            slidingImageDots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SplashActivity,
                    R.drawable.non_active_dot
                )
            )
            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(8, 0, 8, 0)
            binding.sliderDots.addView(slidingImageDots[i], params)
        }

        slidingImageDots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                this@SplashActivity,
                R.drawable.active_dot
            )
        )

    }
}