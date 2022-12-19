package com.fivek.userapp.ui.checkout

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivek.userapp.R
import com.fivek.userapp.app.CarCareApplication
import com.fivek.userapp.databinding.ActivityCheckOutViewBinding
import com.fivek.userapp.ui.BaseActivity
import com.fivek.userapp.ui.checkout.adapter.OutletsAdapter
import com.fivek.userapp.ui.checkout.adapter.PreferredDateAdapter
import com.fivek.userapp.ui.checkout.adapter.TimeSlotsAdapter
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.viewmodel.response.LocationInfoData
import com.fivek.userapp.viewmodel.response.outlets.Outlet
import com.fivek.userapp.viewmodel.response.preferredDate.PreferredDate
import com.fivek.userapp.viewmodel.response.timeslot.Slots
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class CheckOutActivity : BaseActivity() {

    private lateinit var binding: ActivityCheckOutViewBinding
    private lateinit var checkOutViewModel: CheckOutViewModel
    val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    var outletId = 0
    lateinit var selectDate: Calendar
    var timesolt: Int = 0
    var orderPrice = ""
    var bookedDate = ""
    var isElite =false
    var audioId : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectDate = Calendar.getInstance()
        orderPrice = intent.extras?.getString(Constants.ORDER_PRICE).toString()
        audioId= intent.extras?.getString(Constants.AUDIO_ID)
        isElite= intent.extras?.getBoolean(Constants.IS_ELITE) == true
        checkOutViewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        binding.backAction.setOnClickListener {
            finish()
        }
        binding.pickTimeList.layoutManager = LinearLayoutManager(this@CheckOutActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.preferredDateList.layoutManager = LinearLayoutManager(this@CheckOutActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.nearByOutletsList.layoutManager = LinearLayoutManager(this)


        binding.priceAmount.text = orderPrice
//        binding.addressView.setOnClickListener {
//            val intent = Intent(this@CheckOutActivity, MapsActivity::class.java)
//            resultLauncher.launch(intent)
//        }

        binding.checkOutAction.setOnClickListener {
            if(timesolt>0) {
                val intent = Intent(this@CheckOutActivity, PaymentMethodActivity::class.java)
                intent.putExtra(Constants.OUTLET_ID, outletId)
                intent.putExtra(Constants.TIME_SLOT, timesolt)
                intent.putExtra(Constants.PICK_UP_REQUIRED, binding.freePickUp.isChecked)
                intent.putExtra(Constants.BOOKING_DATE,bookedDate)
                intent.putExtra(Constants.AUDIO_ID,audioId)
                startActivity(intent)
            }else{
                showToast("Please select the timeslot")
            }
        }

        binding.freePickUp.setOnClickListener {
            if(binding.freePickUp.isChecked){
                binding.pickView.setBackgroundResource(R.drawable.draw_pick_up_fill_red_view)
                binding.pickupHint.setTextColor(Color.parseColor("#d32f2f"))
                binding.localAddress.setTextColor(Color.parseColor("#000000"))
                binding.forwardArrow.setColorFilter(Color.parseColor("#d32f2f"), PorterDuff.Mode.SRC_ATOP)
            }else {
                binding.pickView.setBackgroundResource(R.drawable.draw_rounded_gray_view)
                binding.pickupHint.setTextColor(Color.parseColor("#828282"))
                binding.localAddress.setTextColor(Color.parseColor("#828282"))
                binding.forwardArrow.setColorFilter(Color.parseColor("#828282"), PorterDuff.Mode.SRC_ATOP)
            }

        }


        binding.localAddress.text = CarCareApplication.instance.locationInfoData.fullAddress

        var preferredDateList = mutableListOf<PreferredDate>()
        for (i in 0..3) {
            val calendar = getCalculatedDate(i)
            preferredDateList.add(PreferredDate(calendar, calendar.get(Calendar.DATE).toString(), days[calendar.get(Calendar.DAY_OF_WEEK) - 1], i == 0))

        }

        val preferredAdapter = PreferredDateAdapter(preferredDateList, object : PreferredDateAdapter.ItemClickListener {
            override fun itemClick(slot: PreferredDate) {
                selectDate = slot.calender
                getTimeSlots()
            }

        })
        binding.preferredDateList.adapter = preferredAdapter

        checkOutViewModel.outletsResponse.observe(this) { response ->

            if (response != null && response.data.isNotEmpty()) {
                for (i in response.data.indices) {
                    if (i == 0) {
                        val item = response.data[i]
                        outletId = item.id
                        item.selected = true
                        response.data[i] = item
                        getTimeSlots()
                        break
                    }
                }
                val outletsAdapter = OutletsAdapter(response.data, object : OutletsAdapter.ItemClickListener {
                    override fun itemClick(slot: Outlet) {
                        outletId = slot.id
                        getTimeSlots()
                    }

                })
                binding.nearByOutletsList.adapter = outletsAdapter
            }


        }

        checkOutViewModel.timeSlotsResponse.observe(this) { response ->

            if (response != null && response.data.slots.isNotEmpty()) {
                binding.noTimeSlot.visibility = View.GONE
                binding.pickTimeList.visibility = View.VISIBLE
                val builder = SpannableStringBuilder()
                val word: Spannable = SpannableString(getString(R.string.pick_time_slot))
                word.setSpan(ForegroundColorSpan(Color.BLACK), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.append(word)
                val wordTwo: Spannable = SpannableString(getString(R.string.slot_available, response.data.slots.size))
                wordTwo.setSpan(ForegroundColorSpan(Color.parseColor("#35C759")), 0, wordTwo.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.append(wordTwo)
                binding.timeSlotCount.setText(builder, TextView.BufferType.SPANNABLE);
                val timeSlotAdapter = TimeSlotsAdapter(response.data.slots, object : TimeSlotsAdapter.ItemClickListener {
                    override fun itemClick(slot: Slots) {
                        timesolt = slot.id
                    }

                })
                binding.pickTimeList.adapter = timeSlotAdapter
            }else {
                binding.noTimeSlot.visibility = View.VISIBLE
                binding.pickTimeList.visibility = View.GONE
            }
        }



        checkOutViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        checkOutViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }

        val query = HashMap<String, Any>()
        query["city"] = "CBE"
        query["lat"] = CarCareApplication.instance.locationInfoData.latitude
        query["long"] = CarCareApplication.instance.locationInfoData.longitude
        query["isElite"] = isElite
        checkOutViewModel.fetchOutLets(query)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                val address = data.getStringExtra(Constants.NEW_ADDRESS_UPDATE)
                address?.let {
                    val data = Gson().fromJson(it, LocationInfoData::class.java)
                    CarCareApplication.instance.locationInfoData = data
                    binding.localAddress.text = data.fullAddress
                }
            }
        }
    }

    fun getCalculatedDate(days: Int): Calendar {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal
    }

    fun getTimeSlots() {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        bookedDate = formatter.format(selectDate.time)
        val query = HashMap<String, Any>()
        query["outletId"] = outletId
        query["date"] = bookedDate
        checkOutViewModel.fetchTimeSlots(query)
    }
}