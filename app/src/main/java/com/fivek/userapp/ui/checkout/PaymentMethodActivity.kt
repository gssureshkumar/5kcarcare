package com.fivek.userapp.ui.checkout

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import com.fivek.userapp.BuildConfig
import com.fivek.userapp.MainActivity
import com.fivek.userapp.R
import com.fivek.userapp.app.CarCareApplication
import com.fivek.userapp.databinding.ActivityPaymentConfirmationBinding
import com.fivek.userapp.ui.BaseActivity
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.utils.PreferenceHelper
import com.fivek.userapp.viewmodel.request.booking.AddBookingRequest
import com.fivek.userapp.viewmodel.response.addBookingResponse.AddBookingResponse
import com.fivek.userapp.viewmodel.response.vouchers.Voucher
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.roundToInt


class PaymentMethodActivity : BaseActivity(), PaymentResultWithDataListener {
    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }

    private lateinit var binding: ActivityPaymentConfirmationBinding
    private lateinit var checkOutViewModel: CheckOutViewModel
    private  var addBookingResponse: AddBookingResponse?=null
    private var serviceIds: MutableList<Int> = mutableListOf()
    var totalAmount = 0.0
    var finalAmount = 0.0
    var paymentMethod = ""
    var vehicleId = ""
    var paymentError: String? = null
    var audioId : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Checkout.preload(this)
        checkOutViewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        binding.backAction.setOnClickListener {
            finish()
        }
        binding.voucherView.setOnClickListener {
            val intent = Intent(this@PaymentMethodActivity, VouchersActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.termsConditions.setOnClickListener {
            loadCustomTabs(Constants.TERMS_CONDITION)
        }


        val outletId = intent.extras?.getInt(Constants.OUTLET_ID)
        val timeSlot = intent.extras?.getInt(Constants.TIME_SLOT)
        val bookingDate = intent.extras?.getString(Constants.BOOKING_DATE)
        val pickUpRequired = intent.extras?.getBoolean(Constants.PICK_UP_REQUIRED)
        audioId= intent.extras?.getString(Constants.AUDIO_ID)



        binding.placeOrderAction.setOnClickListener {

            if(addBookingResponse ==null) {

                if (paymentMethod.isEmpty()) {
                    showToast(getString(R.string.please_select_payment_mode))
                } else if (!binding.checkBoxInput.isChecked) {
                    showToast(getString(R.string.please_select_terms_conditions))
                } else {

                    val body = if (pickUpRequired!!) {
                        AddBookingRequest.BookingRequest(
                            serviceIds,
                            outletId!!,
                            timeSlot!!,
                            paymentMethod,
                            bookingDate!!,
                            vehicleId,
                            pickUpRequired,
                            binding.voucherCode.text.toString(),
                            CarCareApplication.instance.locationInfoData.latitude,
                            CarCareApplication.instance.locationInfoData.longitude,
                            CarCareApplication.instance.locationInfoData.fullAddress,
                            audioId
                        )
                    } else {
                        AddBookingRequest.BookingRequest(
                            serviceIds,
                            outletId!!,
                            timeSlot!!,
                            paymentMethod,
                            bookingDate!!,
                            vehicleId,
                            pickUpRequired,
                            binding.voucherCode.text.toString(),
                            0.0,
                            0.0,
                            "",
                            audioId
                        )
                    }

                    checkOutViewModel.addBookingRequest(body)
                }
            }else {
                startPayment()
            }

        }

        binding.radioGrp.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.onlineBtn -> {
                    paymentMethod = "online"
                    binding.placeOrderAction.text = getString(R.string.pay_now)
                }
                R.id.codBtn -> {
                    paymentMethod = "cod"
                    binding.placeOrderAction.text = getString(R.string.place_order)
                }

            }
        }

        binding.onlineBtn.isChecked = true

        CarCareApplication.instance.repository.primaryVehicle.observe(this) { vehile ->
            vehicleId = vehile.id
        }
        CarCareApplication.instance.cartRepository.cartList.observe(this) { cartList ->
            if (cartList != null && cartList.isNotEmpty()) {
                var priceAmount = 0.0
                var offerAmount = 0.0
                for (item in cartList) {
                    serviceIds.add(item.serviceId)
                    priceAmount += item.price
                    if (item.offer > 0) {
                        offerAmount += item.offer
                    }
                }
                totalAmount = offerAmount

                if (prefsHelper.intUserTypePref != "normal") {
                    val memberValue = ((totalAmount / 100) * 9).roundToInt()
                    totalAmount -= memberValue
                    binding.membershipView.visibility = View.VISIBLE
                    binding.membershipTotal.text = "₹ -${memberValue}"
                } else {
                    binding.membershipView.visibility = View.GONE
                }

                binding.actualPrice.text = "₹ ${priceAmount.roundToInt()}"
                binding.offerPrice.text = "₹ ${offerAmount.roundToInt()}"
                finalAmount = totalAmount

                checkOutViewModel.fetchVouchers()

            }
        }

        checkOutViewModel.paymentStatusResponse.observe(this) { response ->
                moveToNext(getString(R.string.your_booking_has_been_confirmed), true)
        }

        checkOutViewModel.addBookingResponse.observe(this) { response ->
            if (response != null) {
                if (paymentMethod == "online") {
                    addBookingResponse = response
                    startPayment()
                } else {
                    moveToNext(getString(R.string.your_booking_has_been_confirmed) ,true)
                }
            }
        }

        checkOutViewModel.vouchersResponse.observe(this) { response ->
            if (response != null && response.vouches.isNotEmpty()) {
                binding.voucherView.visibility = View.VISIBLE
                binding.voucherAppliedView.visibility = View.VISIBLE
                updateVoucher(response.vouches[0])
            } else {
                binding.voucherView.visibility = View.GONE
                binding.voucherAppliedView.visibility = View.GONE
                updateVoucher(Voucher("", "", "", 0))
            }
        }

        checkOutViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        checkOutViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                val voucher = data.getStringExtra(Constants.VOUCHER)
                voucher?.let {
                    val data = Gson().fromJson(it, Voucher::class.java)
                    updateVoucher(data)
                }
            }
        }
    }

    fun updateVoucher(voucher: Voucher) {
        binding.voucherPrice.text = "₹ ${voucher.value}"
        binding.voucherCode.text = voucher.id
        binding.voucherType.text = voucher.type
        binding.voucherTotal.text = "₹ ${voucher.value}"

        totalAmount = finalAmount - voucher.value
        if (totalAmount < 0) {
            totalAmount = 0.0
        }

        binding.totalPrice.text = "₹ ${totalAmount.roundToInt()}"
        binding.orderAmount.text = "₹ ${totalAmount.roundToInt()}"
    }

    private fun startPayment() {
        if(addBookingResponse !=null) {
            /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
            val co = Checkout()
            co.setKeyID(BuildConfig.RAZOR_KEY)
            try {
                val options = JSONObject()
                options.put("name", prefsHelper.intUserNamePref)
                options.put("description", "To pay")
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "")
                options.put("theme.color", "#0E2D61");
                options.put("currency", "INR");
                options.put("order_id", addBookingResponse!!.data.rzpOrderId);
                options.put("amount", totalAmount * 100)
//            options.put("amount", 100)
                val retryObj = JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 4);
                options.put("retry", retryObj);

                val prefill = JSONObject()
                prefill.put("email", "")
                prefill.put("contact", prefsHelper.intMobileNumberPref)
                options.put("prefill", prefill)
                co.open(this, options)
            } catch (e: Exception) {
                Toast.makeText(
                    this@PaymentMethodActivity,
                    "Error in payment: " + e.message,
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    override fun onPaymentSuccess(p0: String?, paymentData: PaymentData?) {
        val body = paymentData?.let {
            AddBookingRequest.PaymentStatusRequest(
                addBookingResponse!!.data.id,
                paymentData.paymentId,
                "payment"
            )
        }
        if (body != null) {
            checkOutViewModel.paymentStatusRequest(body)
        }else{
            moveToNext(getString(R.string.error_in_payment_gateway, "Failed"), false)
        }

    }

    override fun onPaymentError(p0: Int, error: String?, paymentData: PaymentData?) {
        if (error != null) {
            val json = JSONObject(error)
            if (json.has("error")) {
                val jsonObject = json.getJSONObject("error")
                paymentError = jsonObject.getString("description")
                moveToNext(getString(R.string.error_in_payment_gateway, paymentError), false)
            }

        }
//        val body =
//            AddBookingRequest.PaymentStatusRequest(
//                addBookingResponse.data.id,
//                "",
//                "failed"
//            )
//        checkOutViewModel.paymentStatusRequest(body)
    }

    fun loadCustomTabs(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    fun moveToNext(message: String, isSuccess:Boolean) {
        if(isSuccess) {
            CarCareApplication.instance.applicationScope.launch {
                CarCareApplication.instance.cartRepository.deleteAll()
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.setPositiveButton(R.string.yes) { dialog, which ->
            if(isSuccess) {
                val intent = Intent(this@PaymentMethodActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Constants.BOOKING_ID, addBookingResponse!!.data.id)
                startActivity(intent)
            }


        }

        builder.show()


    }

}