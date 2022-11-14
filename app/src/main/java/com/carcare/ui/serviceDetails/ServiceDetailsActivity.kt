package com.carcare.ui.serviceDetails

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.database.CartModelData
import com.carcare.databinding.ActivityServiceDetailsBinding
import com.carcare.ui.BaseActivity
import com.carcare.ui.cart.CartListActivity
import com.carcare.ui.serviceDetails.adapter.RatingListAdapter
import com.carcare.ui.serviceDetails.adapter.ServiceIncludedAdapter
import com.carcare.utils.Constants.CITY
import com.carcare.utils.Constants.SERVICE_ID
import com.carcare.utils.Constants.STATE
import com.carcare.utils.Constants.VEHICLE_TYPE
import com.carcare.utils.loadImage
import com.carcare.viewmodel.response.servicesDetailsResponse.ServiceDetailsResponse
import kotlinx.coroutines.launch

class ServiceDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityServiceDetailsBinding
    private lateinit var serviceDetailsViewModel: ServiceDetailsViewModel
    private lateinit var serviceDetailsResponse: ServiceDetailsResponse

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        finish()
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceId = intent.extras?.getInt(SERVICE_ID)
        val city = intent.extras?.getString(CITY)
        val state = intent.extras?.getString(STATE)
        val vehicleType = intent.extras?.getString(VEHICLE_TYPE)

        serviceDetailsViewModel = ViewModelProvider(this)[ServiceDetailsViewModel::class.java]
        binding.backIcon.setOnClickListener {
            finish()
        }

        binding.addCartView.setOnClickListener {
            CarCareApplication.instance.applicationScope.launch {
                val cart = CartModelData(serviceDetailsResponse.data.id, serviceDetailsResponse.data.name, serviceDetailsResponse.data.banner, serviceDetailsResponse.data.actual, serviceDetailsResponse.data.offer)
                CarCareApplication.instance.applicationScope.launch {
                    CarCareApplication.instance.cartRepository.insert(cart)
                }
            }
            val intent = Intent(this@ServiceDetailsActivity, CartListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
        binding.servicesList.layoutManager = LinearLayoutManager(this)
        binding.reviewList.layoutManager = LinearLayoutManager(this)


        serviceDetailsViewModel.serviceDetailsResponse.observe(this) { response ->

            if (response != null) {
                binding.addCartView.visibility = View.VISIBLE
                serviceDetailsResponse = response
                binding.bannerImage.loadImage(response.data.banner)
                binding.serviceName.text = response.data.name
                binding.mainContainer.visibility =View.VISIBLE
                val hours: Int = response.data.duration / 60 //since both are ints, you get an int
                val minutes: Int = response.data.duration % 60
                if(hours>0) {
                    binding.timeTaken.text = String.format("%dHr %02dMins", hours, minutes)
                }else {
                    binding.timeTaken.text = String.format("%02dMins", minutes)

                }
                binding.securityTxt.text = getString(R.string.ro_treated_water)
                binding.ratingTxt.text = response.data.rating.toString()
                binding.freePickUp.text = getString(R.string.free_pick_up_drop)
                binding.offerAmount.visibility = View.GONE
                if(response.data.offer>0){
                    binding.offerAmount.visibility = View.VISIBLE
                    binding.priceAmount.text =getString(R.string.offer)+"₹ "+ response.data.offer
                    binding.offerAmount.apply {
                            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            text = "₹ "+ response.data.actual
                        }
                }else {
                    binding.priceAmount.text ="₹ "+ response.data.actual
                }


                if(response.data.includes !=null &&response.data.includes.isNotEmpty()){
                    binding.serviceIncluded.visibility =View.VISIBLE
                    binding.servicesList.visibility =View.VISIBLE
                    val serviceListAdapter = ServiceIncludedAdapter(response.data.includes)
                    binding.servicesList.adapter = serviceListAdapter
                }else {
                    binding.serviceIncluded.visibility =View.GONE
                    binding.servicesList.visibility =View.GONE
                }

                if(response.data.reviews !=null && response.data.reviews.isNotEmpty()){
                    binding.customReview.visibility =View.VISIBLE
                    binding.reviewList.visibility =View.VISIBLE
                    binding.customReviewLine.visibility =View.VISIBLE
                    val reviewAdapter = RatingListAdapter(response.data.reviews)
                    binding.reviewList.adapter = reviewAdapter
                }else{
                    binding.customReview.visibility =View.GONE
                    binding.reviewList.visibility =View.GONE
                    binding.customReviewLine.visibility =View.GONE
                }

            }

        }
        serviceDetailsViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        serviceDetailsViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }

        val query = HashMap<String, Any>()
        query["id"] = serviceId!!
        query["city"] = city!!
//        query["city"] ="Coimbatore"
            query["state"] = "TN"
        query["vehicleType"] = vehicleType!!

        serviceDetailsViewModel.fetchDetailsResponse(query)

    }
}