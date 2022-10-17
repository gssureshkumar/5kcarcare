package com.carcare.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcare.app.CarCareApplication
import com.carcare.databinding.ActivityPaymentConfirmationBinding
import com.carcare.databinding.ActivityVouchersBinding
import com.carcare.ui.BaseActivity
import com.carcare.ui.checkout.adapter.VouchersAdapter
import com.carcare.utils.Constants
import com.carcare.viewmodel.response.vouchers.Voucher
import com.google.gson.Gson

class VouchersActivity: BaseActivity() {

    private lateinit var binding: ActivityVouchersBinding
    private lateinit var checkOutViewModel: CheckOutViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVouchersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkOutViewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        binding.backAction.setOnClickListener {
            finish()
        }
        binding.vouchersList.layoutManager = LinearLayoutManager(this)

        checkOutViewModel.vouchersResponse.observe(this) { response ->
            if (response != null && response.vouches.isNotEmpty()) {
                val adapter = VouchersAdapter(response.vouches, object :VouchersAdapter.ItemClickListener{
                    override fun itemClick(voucher: Voucher) {
                        val data = Intent()
                        data.putExtra(Constants.VOUCHER, Gson().toJson(voucher))
                        setResult(RESULT_OK, data)
                        finish()
                    }

                })

                binding.vouchersList.adapter = adapter
            }
        }



        checkOutViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        checkOutViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }

        checkOutViewModel.fetchVouchers()
    }
}