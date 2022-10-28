package com.carcare.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.carcare.MainActivity
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.database.VehicleModel
import com.carcare.databinding.FragmentHomeBinding
import com.carcare.ui.home.adapter.OtherServicesAdapter
import com.carcare.ui.home.adapter.ServiceListAdapter
import com.carcare.ui.home.banner.BannerImagesAdapter
import com.carcare.ui.serviceDetails.ServiceDetailsActivity
import com.carcare.ui.setaddress.MapsActivity
import com.carcare.utils.Constants
import com.carcare.utils.Constants.CITY
import com.carcare.utils.Constants.SERVICE_ID
import com.carcare.utils.Constants.STATE
import com.carcare.utils.Constants.VEHICLE_TYPE
import com.carcare.utils.PreferenceHelper
import com.carcare.utils.TutorialDataManager
import com.carcare.viewmodel.request.vehicle.VehicleRequest
import com.carcare.viewmodel.response.LocationInfoData
import com.carcare.viewmodel.response.services.ServiceData
import com.google.android.datatransport.cct.internal.LogResponse.fromJson
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var _binding: FragmentHomeBinding
    private var  infoData :LocationInfoData? = null
    private var vehicleType = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    private var currentPage = 0
    private lateinit var slidingImageDots: Array<ImageView?>
    private var slidingDotsCount = 0
    private var handler = Handler()
    private var timer = Timer()

    private val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            for (i in 0 until slidingDotsCount) {
                slidingImageDots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.non_active_dot
                    )
                )
            }

            slidingImageDots[position]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        val address =  (activity as? MainActivity)?.getCurrentAddress()
        updateAddress(address)

        _binding.addressView.setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            resultLauncher.launch(intent)
        }

        _binding.carTypeView.setOnClickListener {
            _binding.addVehicle.performClick()
        }
        _binding.addVehicle.setOnClickListener {
             (activity as? MainActivity)?.showMyVehicle()
        }

        _binding.btnReferNow.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.use_referral_code_txt, prefsHelper.intRefCodePref, "₹500"));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)))
        }

        _binding.ourServiceList.layoutManager = GridLayoutManager(requireActivity(), 4)
        _binding.trendingServicesList.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        _binding.recommendedList.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)



        CarCareApplication.instance.repository.primaryVehicle.observe(requireActivity()) { vehile ->
            if (vehile?.type != null) {
                vehicleType = vehile.type
                when {
                    vehile.type.lowercase(Locale.getDefault()).contains("hatch") -> {
                        binding.carType.setImageResource(R.drawable.ic_small_hatch_icon)
                    }
                    vehile.type.lowercase(Locale.getDefault()).contains("sedan") -> {
                        binding.carType.setImageResource(R.drawable.ic_small_sedan_icon)
                    }
                    vehile.type.lowercase(Locale.getDefault()).contains(getString(R.string.suv).lowercase(Locale.getDefault())) -> {
                        binding.carType.setImageResource(R.drawable.ic_small_suv_icon)
                    }
                    vehile.type.lowercase(Locale.getDefault()).contains("7seater") -> {
                        binding.carType.setImageResource(R.drawable.ic_small_seaters_icon)
                    }
                }
                binding.addVehicle.text = vehile.carModel
            } else {
                _binding.addVehicle.performClick()
            }
        }

        homeViewModel.dashBoardResponse.observe(requireActivity()) { response ->

            if (response != null) {
                RecommendedServiceResponse.getServiceResponse = response
                if(response.data.offers.size>0) {
                    TutorialDataManager.offersList = response.data.offers
                    setUpSlidingViewPager()
                    _binding.promotionContainer.visibility = View.VISIBLE
                }else {
                    _binding.promotionContainer.visibility = View.GONE
                }
                if (response.data.others.isNotEmpty()) {
                    val serviceListAdapter = ServiceListAdapter(response.data.others, object :ServiceListAdapter.ItemClickListener{
                        override fun itemClick(data: ServiceData) {
                            if(infoData !=null) {
                                val intent = Intent(requireActivity(), ServiceDetailsActivity::class.java)
                                intent.putExtra(SERVICE_ID, data.id)
                                intent.putExtra(CITY, infoData!!.city)
                                intent.putExtra(STATE, infoData!!.state)
                                intent.putExtra(VEHICLE_TYPE, vehicleType)
                                startActivity(intent)
                            }
                        }

                    })
                    _binding.ourServiceList.adapter = serviceListAdapter
                }

                if (response.data.trending.isNotEmpty()) {
                    val serviceListAdapter = OtherServicesAdapter(response.data.trending, object :OtherServicesAdapter.ItemClickListener{
                        override fun itemClick(data: ServiceData) {
                            if(infoData !=null) {
                                val intent = Intent(requireActivity(), ServiceDetailsActivity::class.java)
                                intent.putExtra(SERVICE_ID, data.id)
                                intent.putExtra(CITY, infoData!!.city)
                                intent.putExtra(STATE, infoData!!.state)
                                intent.putExtra(VEHICLE_TYPE, vehicleType)
                                startActivity(intent)
                            }
                        }

                    })
                    _binding.trendingServicesList.adapter = serviceListAdapter
                }

                if (response.data.recommended.isNotEmpty()) {
                    val serviceListAdapter = OtherServicesAdapter(response.data.recommended, object :OtherServicesAdapter.ItemClickListener{
                        override fun itemClick(data: ServiceData) {
                            if(infoData !=null) {
                                val intent = Intent(requireActivity(), ServiceDetailsActivity::class.java)
                                intent.putExtra(SERVICE_ID, data.id)
                                intent.putExtra(CITY, infoData!!.city)
                                intent.putExtra(STATE, infoData!!.state)
                                intent.putExtra(VEHICLE_TYPE, vehicleType)
                                startActivity(intent)
                            }
                        }

                    })
                    _binding.recommendedList.adapter = serviceListAdapter
                }


            }

        }


        homeViewModel.isLoading.observe(requireActivity()) { isLoading ->
             (activity as? MainActivity)?.setDialog(isLoading)
        }
        homeViewModel.errorMessage.observe(requireActivity()) { errorMessage ->
             (activity as? MainActivity)?.showToast(errorMessage.toString())
        }

        homeViewModel.fetchDashBoardResponse()
    }


    private fun setUpSlidingViewPager() {
        timer = Timer()
        handler = Handler()
        slidingDotsCount = TutorialDataManager.getOffersList.size

        val landingImagesAdapter =
            BannerImagesAdapter(requireActivity() as AppCompatActivity, slidingDotsCount)
        _binding.slidingViewPager.apply {
            adapter = landingImagesAdapter
            registerOnPageChangeCallback(slidingCallback)
        }


        slidingImageDots = arrayOfNulls(slidingDotsCount)

        _binding.sliderDots.removeAllViews()
        for (i in 0 until slidingDotsCount) {
            slidingImageDots[i] = ImageView(requireContext())
            slidingImageDots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.non_active_dot
                )
            )
            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(8, 0, 8, 0)
            _binding.sliderDots.addView(slidingImageDots[i], params)
        }

        slidingImageDots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )


        val update = Runnable {
            if (currentPage == slidingDotsCount) {
                currentPage = 0
            }
            try {
                _binding.slidingViewPager.setCurrentItem(currentPage++, true)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3500, 3500)


    }

    fun updateAddress(infoData: LocationInfoData?) {
        this.infoData =infoData
        _binding.localAddress.visibility = View.INVISIBLE
        _binding.loadingView.visibility = View.VISIBLE
        _binding.loadingView.text = getString(R.string.loading)
        if (infoData !=null) {
            _binding.localAddress.visibility = View.VISIBLE
            _binding.localAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pickup_pin_icon, 0, 0, 0)
            _binding.localAddress.text = infoData.locality
            _binding.loadingView.text = infoData.fullAddress
        }

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                val address = data.getStringExtra(Constants.NEW_ADDRESS_UPDATE)
                address?.let {
                    val data = Gson().fromJson(it, LocationInfoData::class.java)
                    updateAddress(data) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}