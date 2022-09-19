package com.carcare

import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.carcare.databinding.ActivityMainBinding
import com.carcare.location.EasyWayLocation
import com.carcare.location.EasyWayLocation.LOCATION_SETTING_REQUEST_CODE
import com.carcare.location.GetLocationDetail
import com.carcare.location.Listener
import com.carcare.location.LocationData
import com.carcare.ui.BaseActivity
import com.carcare.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity(), Listener, LocationData.AddressCallBack {

    private lateinit var binding: ActivityMainBinding
    private lateinit var easyWayLocation: EasyWayLocation
    private lateinit var getLocationDetail: GetLocationDetail
    private var currentAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocationDetail = GetLocationDetail(this, this)
        easyWayLocation = EasyWayLocation(this, false, this)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        getLocationResult()
    }


    fun getLocationResult() {
        checkLocationPermission(object : PermissionListener {
            override fun permissionGranted() {
                easyWayLocation.startLocation()
            }

            override fun permissionDenied() {
                easyWayLocation.showAlertDialog(getString(R.string.location_error_message))
            }

        })
    }

    override fun locationOn() {
        getLocationResult()
    }

    override fun currentLocation(location: Location?) {
        if (location != null) {
            val locationInfoData = easyWayLocation.getAddress(this@MainActivity, location.latitude, location.longitude)
            currentAddress = locationInfoData.fullAddress
            val fragment = getForegroundFragment()
            if (fragment is HomeFragment) {
                fragment.updateAddess(locationInfoData.locality)
            }
        }
    }

    override fun locationCancelled() {
        easyWayLocation.showAlertDialog(getString(R.string.location_error_message))
    }

    override fun locationData(locationData: LocationData?) {


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTING_REQUEST_CODE) {
            easyWayLocation.onActivityResult(resultCode)
        }
    }

    override fun onResume() {
        super.onResume()
        easyWayLocation.startLocation()
    }

    override fun onPause() {
        super.onPause()
        easyWayLocation.endUpdates()
    }

    fun getCurrentAddress(): String {
        return currentAddress
    }

    fun getForegroundFragment(): Fragment {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)!!
    }
}