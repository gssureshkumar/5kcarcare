package com.carcare.ui.checkout

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.databinding.ActivityCheckOutViewBinding
import com.carcare.databinding.ActivityPaymentConfirmationBinding
import com.carcare.ui.BaseActivity
import com.carcare.ui.cart.adapter.CartListAdapter
import com.carcare.ui.checkout.adapter.TimeSlotsAdapter
import com.carcare.ui.setaddress.MapsActivity
import com.carcare.utils.Constants
import com.carcare.viewmodel.response.LocationInfoData
import com.carcare.viewmodel.response.timeslot.Slots
import com.carcare.viewmodel.response.vouchers.Voucher
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

class PaymentMethodActivity : BaseActivity() {

    private lateinit var binding: ActivityPaymentConfirmationBinding
    private lateinit var checkOutViewModel: CheckOutViewModel
    var totalAmount = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkOutViewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        binding.backAction.setOnClickListener {
            finish()
        }
        binding.voucherView.setOnClickListener {
            val intent = Intent(this@PaymentMethodActivity, VouchersActivity::class.java)
            resultLauncher.launch(intent)
        }

        CarCareApplication.instance.cartRepository.cartList.observe(this) { cartList ->
            if(cartList !=null&& cartList.isNotEmpty()){
                var priceAmount = 0.0
                var offerAmount = 0.0
                for (item in cartList){
                    priceAmount += item.price
                    if(item.offer>0) {
                        offerAmount += (item.price-item.offer)
                    }
                }
                totalAmount = priceAmount- offerAmount
                binding.actualPrice.text = "₹ $priceAmount"
                binding.offerPrice.text = "₹ $offerAmount"
                checkOutViewModel.fetchVouchers()

            }
        }

        checkOutViewModel.vouchersResponse.observe(this) { response ->
            if (response != null && response.vouches.isNotEmpty()) {
                updateVoucher(response.vouches[0])
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
                    updateVoucher(data) }
            }
        }
    }

    fun updateVoucher( voucher: Voucher){
        binding.voucherPrice.text = "₹ ${voucher.value}"
        binding.voucherCode.text =voucher.id
        binding.voucherTotal.text = "₹ ${voucher.value}"
        totalAmount -= voucher.value
        binding.totalPrice.text = "₹ $totalAmount"
        binding.orderAmount.text = "₹ $totalAmount"
    }
}