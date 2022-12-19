package com.fivek.userapp

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fivek.userapp.app.CarCareApplication
import com.fivek.userapp.database.VehicleModel
import com.fivek.userapp.databinding.ActivityMainBinding
import com.fivek.userapp.location.AddressCallBack
import com.fivek.userapp.location.FetchLocation
import com.fivek.userapp.location.FetchLocation.LOCATION_SETTING_REQUEST_CODE
import com.fivek.userapp.location.GetLocationDetail
import com.fivek.userapp.location.Listener
import com.fivek.userapp.ui.BaseActivity
import com.fivek.userapp.ui.authentication.NameUpdateBottomSheetFragment
import com.fivek.userapp.ui.bookingDetails.BookingDetailsActivity
import com.fivek.userapp.ui.home.AddCarModelBottomSheetFragment
import com.fivek.userapp.ui.home.HomeFragment
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.utils.PreferenceHelper
import com.fivek.userapp.viewmodel.request.LoginRequestBodies
import com.fivek.userapp.viewmodel.request.vehicle.VehicleRequest
import com.fivek.userapp.viewmodel.response.LocationInfoData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.DynamicLink.IosParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.launch


class MainActivity : BaseActivity(), Listener, AddressCallBack {
    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var fetchLocation: FetchLocation
    private lateinit var getLocationDetail: GetLocationDetail
    private lateinit var navView: BottomNavigationView
    private var vehicleModel: VehicleModel? = null
    private var currentAddress = ""
    private var locationInfoData: LocationInfoData? = null
    private var userName = ""
    private var deleteId = ""
    private var isApiCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        getLocationDetail = GetLocationDetail(this, this)
        fetchLocation = FetchLocation(this, false, this)

        navView = binding.navView
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

        mainViewModel.addVehicleResponse.observe(this) { response ->

            CarCareApplication.instance.applicationScope.launch {
                val vehicle = VehicleModel(
                    response.data.id,
                    response.data.type,
                    response.data.primary,
                    response.data.model,
                    response.data.reg_no
                )
                CarCareApplication.instance.applicationScope.launch {
                    CarCareApplication.instance.repository.insert(vehicle)
                }
            }
        }
        mainViewModel.deleteVehicleResponse.observe(this) { response ->

            CarCareApplication.instance.applicationScope.launch {
                CarCareApplication.instance.repository.delete(deleteId)
            }

        }

        mainViewModel.updatePrimaryVehicleResponse.observe(this) { response ->
            if (response != null) {
                CarCareApplication.instance.applicationScope.launch {
                    vehicleModel?.let {
                        vehicleModel!!.isPrimary = true
                        CarCareApplication.instance.repository.insert(it)
                    }
                }
            }

        }
        mainViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        mainViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())

        }
        mainViewModel.errorUpdateMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
            val fragment = getForegroundFragment()
            if (fragment is HomeFragment) {
                fragment.updateVehicleType()
            }


        }

        if (prefsHelper.intShortLinkPref.isNullOrEmpty()) {
            val link = "https://carcare.page.link/finally?invitedby=" + prefsHelper.intRefCodePref
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://carcare.page.link")
                .setAndroidParameters(
                    AndroidParameters.Builder(BuildConfig.APPLICATION_ID)
                        .setMinimumVersion(1)
                        .build()
                )
                .setIosParameters(
                    IosParameters.Builder("com.5k.userapp").setAppStoreId("6443796046")
                        .setMinimumVersion("1.0.1").build()
                )
                .buildShortDynamicLink()
                .addOnSuccessListener { shortDynamicLink ->
                    prefsHelper.intShortLinkPref = shortDynamicLink.shortLink?.toString()

                }.addOnFailureListener {
                    it.printStackTrace()
                }
        }

        val bookingId = intent.extras?.getString(Constants.BOOKING_ID)

        if (!bookingId.isNullOrEmpty()) {
            val intent = Intent(this@MainActivity, BookingDetailsActivity::class.java)
            intent.putExtra(Constants.BOOKING_ID, bookingId)
            startActivity(intent)
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


    fun moveToNext(selectedItemId: Int) {
        navView.selectedItemId = selectedItemId
    }


    override fun locationOn() {
        getLocationResult()
    }

    override fun currentLocation(location: Location?) {
//        if (location != null && !isApiCalled) {
//            if (currentAddress.isEmpty()) {
//                isApiCalled = true
//                getLocationDetail.getAddressFromApi(
//                    location.latitude,
//                    location.longitude,
//                    getString(R.string.google_maps_key)
//                )
//            }
//        }

        if (location != null) {
            if (currentAddress.isEmpty()) {
                locationInfoData = fetchLocation.getAddress(this@MainActivity, location.latitude, location.longitude)
                if (locationInfoData != null) {
                    CarCareApplication.instance.locationInfoData = locationInfoData!!
                    currentAddress = locationInfoData!!.fullAddress
                    val fragment = getForegroundFragment()
                    if (fragment is HomeFragment) {
                        fragment.updateAddress(locationInfoData)
                    }
                    val body = prefsHelper.intDeviceTokenPref?.let { LoginRequestBodies.UpdateProfileBody(locationInfoData!!.latitude, locationInfoData!!.longitude, locationInfoData!!.fullAddress, locationInfoData!!.city, locationInfoData!!.state, "android", it) }
                    body?.let { mainViewModel.updateProfile(it) }
                }else {
                    locationCancelled()
                }
            }
        }
    }

    override fun locationCancelled() {
        fetchLocation.showAlertDialog(getString(R.string.location_error_message))
    }

    override fun locationData(locationData: LocationInfoData?) {
        locationInfoData = locationData
        if (locationInfoData != null) {
            CarCareApplication.instance.locationInfoData = locationInfoData!!
            currentAddress = locationInfoData!!.fullAddress
            val fragment = getForegroundFragment()
            if (fragment is HomeFragment) {
                fragment.updateAddress(locationInfoData)
            }
            val body = prefsHelper.intDeviceTokenPref?.let {
                LoginRequestBodies.UpdateProfileBody(
                    locationInfoData!!.latitude,
                    locationInfoData!!.longitude,
                    locationInfoData!!.fullAddress,
                    locationInfoData!!.city,
                    locationInfoData!!.state,
                    "android",
                    it
                )
            }
            body?.let { mainViewModel.updateProfile(it) }
        } else {
            locationCancelled()
        }

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
        if (locationInfoData != null && currentAddress.isNotEmpty()) {
            return locationInfoData!!
        }
        return null
    }

    fun getForegroundFragment(): Fragment {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)!!
    }

    fun showMyVehicle() {
        val fragment =
            AddCarModelBottomSheetFragment.newInstance(object :
                AddCarModelBottomSheetFragment.ItemClickListener {
                override fun onSubmitClick(vehicleModel: VehicleModel) {

                    CarCareApplication.instance.applicationScope.launch {
                        CarCareApplication.instance.cartRepository.deleteAll()
                    }
                    mainViewModel.addVehicleRequest(
                        VehicleRequest.AddVehicleRequest(
                            vehicleModel.carModel!!,
                            vehicleModel.type!!,
                            vehicleModel.registration!!,
                            vehicleModel.isPrimary!!,
                            ""
                        )
                    )
                }

                override fun onItemClick(model: VehicleModel) {

                    CarCareApplication.instance.applicationScope.launch {
                        CarCareApplication.instance.cartRepository.deleteAll()
                    }

                    vehicleModel = model
                    val body = VehicleRequest.primaryVehicle(model.id)
                    mainViewModel.updatePrimaryVehicle(body)
                }

                override fun deleteVehicle(id: String) {
                    deleteId = id
                    mainViewModel.deleteVehicleRequest(VehicleRequest.DeleteVehicleRequest(id))
                }
            })
        fragment.isCancelable = false
        fragment.show(supportFragmentManager, fragment.tag)
    }
    override fun onError(message: String?) {
        isApiCalled = false
        fetchLocation.showAlertDialog(message)
    }

    fun shareToOtherApp() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.use_referral_code_txt, "₹100", prefsHelper.intShortLinkPref)
        );
        startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)))


    }
}