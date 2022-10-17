package com.carcare.ui.checkout

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.databinding.ActivityCheckOutViewBinding
import com.carcare.ui.BaseActivity
import com.carcare.ui.checkout.adapter.OutletsAdapter
import com.carcare.ui.checkout.adapter.PreferredDateAdapter
import com.carcare.ui.checkout.adapter.TimeSlotsAdapter
import com.carcare.ui.setaddress.MapsActivity
import com.carcare.utils.Constants
import com.carcare.viewmodel.response.LocationInfoData
import com.carcare.viewmodel.response.outlets.Outlet
import com.carcare.viewmodel.response.preferredDate.PreferredDate
import com.carcare.viewmodel.response.timeslot.Slots
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectDate = Calendar.getInstance()
        orderPrice = intent.extras?.getString(Constants.ORDER_PRICE).toString()
        checkOutViewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        binding.backAction.setOnClickListener {
            finish()
        }
        binding.pickTimeList.layoutManager = LinearLayoutManager(this@CheckOutActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.preferredDateList.layoutManager = LinearLayoutManager(this@CheckOutActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.nearByOutletsList.layoutManager = LinearLayoutManager(this)


        binding.priceAmount.text = orderPrice
        binding.addressView.setOnClickListener {
            val intent = Intent(this@CheckOutActivity, MapsActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.checkOutAction.setOnClickListener {
            if(timesolt>0) {
                val intent = Intent(this@CheckOutActivity, PaymentMethodActivity::class.java)
                startActivity(intent)
            }else{
                showToast("Please select the timeslot")
            }
        }
        binding.localAddress.text = CarCareApplication.instance.locationInfoData.fullAddress

        var preferredDateList = mutableListOf<PreferredDate>()
        for (i in 0..5) {
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
        val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        val output: String = formatter.format(selectDate.time)
        val query = HashMap<String, Any>()
        query["outletId"] = outletId
        query["date"] = output
        checkOutViewModel.fetchTimeSlots(query)
    }
}