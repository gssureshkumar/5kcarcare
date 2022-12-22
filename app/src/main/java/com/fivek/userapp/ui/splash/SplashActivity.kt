package com.fivek.userapp.ui.splash

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.fivek.userapp.MainActivity
import com.fivek.userapp.R
import com.fivek.userapp.app.CarCareApplication
import com.fivek.userapp.databinding.ActivitySplashBinding
import com.fivek.userapp.location.AddressCallBack
import com.fivek.userapp.location.FetchLocation
import com.fivek.userapp.location.GetLocationDetail
import com.fivek.userapp.location.Listener
import com.fivek.userapp.ui.BaseActivity
import com.fivek.userapp.ui.authentication.LoginActivity
import com.fivek.userapp.ui.home.HomeFragment
import com.fivek.userapp.ui.splash.adapter.SlidingImagesAdapter
import com.fivek.userapp.utils.PreferenceHelper
import com.fivek.userapp.utils.TutorialDataManager
import com.fivek.userapp.viewmodel.request.LoginRequestBodies
import com.fivek.userapp.viewmodel.response.LocationInfoData
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule


class SplashActivity : BaseActivity(), Listener, AddressCallBack {
    private lateinit var slidingImageDots: Array<ImageView?>
    private var slidingDotsCount = 0
    private lateinit var binding: ActivitySplashBinding
    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
    private lateinit var fetchLocation: FetchLocation
    private lateinit var getLocationDetail: GetLocationDetail
    private var isApiCalled = false
    private val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {

            if (position == (slidingDotsCount - 1)) {
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
        FirebaseApp.initializeApp(this@SplashActivity)
        TutorialDataManager.init(this@SplashActivity)
        setUpSlidingViewPager()


        getLocationDetail = GetLocationDetail(this, this)
        fetchLocation = FetchLocation(this, false, this)
        binding.skipAction.setOnClickListener {
            prefsHelper.intTutorialPref = true
            moveToLogin()
        }


        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token = task.result!!
                        prefsHelper.intDeviceTokenPref = token
                    }
                }
            }

//        prefsHelper.intAuthTokenPref = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJjODI0YTViNi0yMzY2LTQyMzUtODM2NC1jMGI2ODBkYTE2NjEiLCJpc3MiOiJmdmsiLCJhdWQiOiJmdmsiLCJleHAiOjE2NTg2MTQ3NTgsImlhdCI6MTY2ODAwOTk1OH0.2kT_VI_PNHUz4Pm04iatXHeOXDmxBjTdGOQNtysJANM"

        CarCareApplication.instance.applicationScope.launch {
            CarCareApplication.instance.cartRepository.deleteAll()
        }

        binding.splashView.visibility = View.VISIBLE
        binding.fetchingAddress.visibility = View.VISIBLE
        binding.promotionContainer.visibility = View.GONE
//        Timer().schedule(2000) {
//            runOnUiThread {
//                if (prefsHelper.intTutorialPref) {
//                    if (prefsHelper.intUserIDPref.isNullOrEmpty()) {
//                        moveToLogin()
//                    } else {
//                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                } else {
//                    binding.splashView.visibility = View.GONE
//                    binding.promotionContainer.visibility = View.VISIBLE
//                }
//            }
//        }

        getLocationResult()
    }

    fun moveToLogin() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this@SplashActivity) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                var referrerUid: String? = null
                if (deepLink != null && deepLink.getBooleanQueryParameter("invitedby", false)) {
                    referrerUid = deepLink.getQueryParameter("invitedby")
                    prefsHelper.intUserReferPref = referrerUid
                }

                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
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
    override fun locationOn() {
        getLocationResult()
    }

    override fun currentLocation(location: Location?) {
        if (location != null && !isApiCalled) {
                isApiCalled = true
                getLocationDetail.getAddressFromApi(
                    location.latitude,
                    location.longitude,
                    getString(R.string.google_maps_key)
                )
        }


    }

    override fun locationCancelled() {
        fetchLocation.showAlertDialog(getString(R.string.location_error_message))
    }

    override fun locationData(locationInfoData: LocationInfoData?) {
        if (locationInfoData != null) {
            CarCareApplication.instance.locationInfoData = locationInfoData
            if (prefsHelper.intTutorialPref) {
                if (prefsHelper.intUserIDPref.isNullOrEmpty()) {
                    moveToLogin()
                } else {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                binding.splashView.visibility = View.GONE
                binding.fetchingAddress.visibility = View.GONE
                binding.promotionContainer.visibility = View.VISIBLE
            }
        } else {
            locationCancelled()
        }

    }

    override fun onError(message: String?) {
        isApiCalled = false
        fetchLocation.showAlertDialog(message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FetchLocation.LOCATION_SETTING_REQUEST_CODE) {
            fetchLocation.onActivityResult(resultCode)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchLocation.startLocation()
    }

    override fun onPause() {
        super.onPause()
        fetchLocation.endUpdates()
    }

    fun getLocationResult() {
        checkLocationPermission(object : PermissionListener {
            override fun permissionGranted() {
                fetchLocation.startLocation()
            }

            override fun permissionDenied() {
                fetchLocation.showAlertDialog(getString(R.string.location_error_message))
            }

        })
    }
}