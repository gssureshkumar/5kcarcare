package com.carcare

import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.carcare.app.CarCareApplication
import com.carcare.databinding.ActivityMainBinding
import com.carcare.location.FetchLocation
import com.carcare.location.FetchLocation.LOCATION_SETTING_REQUEST_CODE
import com.carcare.location.GetLocationDetail
import com.carcare.location.Listener
import com.carcare.location.LocationData
import com.carcare.ui.BaseActivity
import com.carcare.ui.authentication.NameUpdateBottomSheetFragment
import com.carcare.ui.home.HomeFragment
import com.carcare.utils.PreferenceHelper
import com.carcare.viewmodel.request.LoginRequestBodies
import com.carcare.viewmodel.response.LocationInfoData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : BaseActivity(), Listener, LocationData.AddressCallBack {
    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var fetchLocation: FetchLocation
    private lateinit var getLocationDetail: GetLocationDetail
    private var currentAddress = ""
    private  var locationInfoData : LocationInfoData? = null
    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        getLocationDetail = GetLocationDetail(this, this)
        fetchLocation = FetchLocation(this, false, this)

        val navView: BottomNavigationView = binding.navView
        navView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        getLocationResult()

        if (prefsHelper.intUserNamePref?.isEmpty() == true) {
            val fragment =
                NameUpdateBottomSheetFragment.newInstance(object :
                    NameUpdateBottomSheetFragment.ItemClickListener {
                    override fun onSubmitClick(name: String) {
                        userName = name
                        val body = LoginRequestBodies.UpdateNameBody(
                            prefsHelper.intUserIDPref!!,
                            userName
                        )
                        mainViewModel.updateUserName(body)
                    }
                })
            fragment.isCancelable = false
            fragment.show(supportFragmentManager, fragment.tag)
        }

        mainViewModel.updateUserNameResponse.observe(this) { response ->
            prefsHelper.intUserNamePref = userName
        }
        mainViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        mainViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }
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

    override fun locationOn() {
        getLocationResult()
    }

    override fun currentLocation(location: Location?) {
        if (location != null) {
            if (currentAddress.isEmpty()) {
                locationInfoData = fetchLocation.getAddress(this@MainActivity, location.latitude, location.longitude)
                CarCareApplication.instance.locationInfoData = locationInfoData!!
                if(locationInfoData !=null) {
                    currentAddress = locationInfoData!!.fullAddress
                    val fragment = getForegroundFragment()
                    if (fragment is HomeFragment) {
                        fragment.updateAddress(locationInfoData)
                    }
                    val body = prefsHelper.intDeviceTokenPref?.let { LoginRequestBodies.UpdateProfileBody(locationInfoData!!.latitude, locationInfoData!!.longitude, locationInfoData!!.fullAddress, locationInfoData!!.city, locationInfoData!!.state, "android", it) }
                    body?.let { mainViewModel.updateProfile(it) }
                }
            }
        }
    }

    override fun locationCancelled() {
        fetchLocation.showAlertDialog(getString(R.string.location_error_message))
    }

    override fun locationData(locationData: LocationData?) {


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTING_REQUEST_CODE) {
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

    fun getCurrentAddress(): LocationInfoData? {
        if(locationInfoData !=null && currentAddress.isNotEmpty()) {
            return locationInfoData!!
        }
        return null
    }

    fun getForegroundFragment(): Fragment {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)!!
    }
}