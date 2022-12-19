package com.fivek.userapp.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivek.userapp.databinding.ActivityVouchersBinding
import com.fivek.userapp.ui.BaseActivity
import com.fivek.userapp.ui.checkout.adapter.VouchersAdapter
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.viewmodel.response.vouchers.Voucher
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
                binding.noDataFound.visibility =View.GONE
                binding.voucherView.visibility =View.VISIBLE
                val adapter = VouchersAdapter(response.vouches, object : VouchersAdapter.ItemClickListener{
                    override fun itemClick(voucher: Voucher) {
                        val data = Intent()
                        data.putExtra(Constants.VOUCHER, Gson().toJson(voucher))
                        setResult(RESULT_OK, data)
                        finish()
                    }

                })

                binding.vouchersList.adapter = adapter
            }else {
                binding.noDataFound.visibility =View.VISIBLE
                binding.voucherView.visibility =View.GONE
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