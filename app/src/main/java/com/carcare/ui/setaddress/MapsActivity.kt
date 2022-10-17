package com.carcare.ui.setaddress

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import com.carcare.R
import com.carcare.databinding.ActivityMapsLocationBinding
import com.carcare.location.FetchLocation
import com.carcare.location.Listener
import com.carcare.ui.BaseActivity
import com.carcare.utils.Constants
import com.carcare.viewmodel.response.LocationInfoData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


class MapsActivity : BaseActivity(), OnMapReadyCallback, Listener {

    lateinit var binding: ActivityMapsLocationBinding
    lateinit var fetchLocation: FetchLocation
    lateinit var driverCurrentLocation: Location
    private lateinit var mMap: GoogleMap
    private var isInitialUpdate :Boolean = false
    lateinit var locationInfoData: LocationInfoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fetchLocation = FetchLocation(this, false, this)
        setLocation(getString(R.string.loading))
        getLocationResult()
        binding.currentLocation.visibility = View.GONE
        binding.currentLocation.setOnClickListener {
            animateCamera(LatLng(driverCurrentLocation.latitude, driverCurrentLocation.longitude))
        }
        binding.btnConfirmAction.setOnClickListener {
            if(locationInfoData !=null) {
                val data = Intent()
                data.putExtra(Constants.NEW_ADDRESS_UPDATE, Gson().toJson(locationInfoData))
                setResult(RESULT_OK, data)
                finish()
            }
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        mMap.setOnCameraMoveListener {
            setLocation(getString(R.string.loading))
        }

        mMap.setOnCameraIdleListener {
            val location = mMap.cameraPosition.target
            locationInfoData = fetchLocation.getAddress(this@MapsActivity, location.latitude, location.longitude)
            setLocation(locationInfoData.fullAddress)
        }

    }


    override fun currentLocation(location: Location?) {
        try {
            location?.let {
                driverCurrentLocation = it
                if(!isInitialUpdate) {
                    binding.currentLocation.visibility = View.VISIBLE
                    animateCamera(LatLng(location.latitude, location.longitude))
                    isInitialUpdate = true
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun animateCamera(location: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18.0f))
    }


    override fun locationCancelled() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FetchLocation.LOCATION_SETTING_REQUEST_CODE) {
            fetchLocation.onActivityResult(resultCode)
        }
    }

    fun setLocation(location: String) {
        binding.carLocationTxt.text = location
        if (location.isEmpty() || location == getString(R.string.loading)) {
            binding.btnConfirmAction.isEnabled =false
            binding.carLocationTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            binding.btnConfirmAction.isEnabled =true
            binding.carLocationTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_green_tick_icon, 0, 0, 0)
        }

    }

}
