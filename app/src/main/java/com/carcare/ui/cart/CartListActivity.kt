package com.carcare.ui.cart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener
import com.carcare.app.CarCareApplication
import com.carcare.databinding.ActivityCartListBinding
import com.carcare.ui.BaseActivity
import com.carcare.ui.cart.adapter.CartListAdapter
import com.carcare.ui.checkout.CheckOutActivity
import com.carcare.ui.home.RecommendedServiceResponse
import com.carcare.ui.home.adapter.ServiceListAdapter
import com.carcare.ui.serviceDetails.ServiceDetailsActivity
import com.carcare.utils.Constants
import com.carcare.viewmodel.response.services.ServiceData
import kotlinx.coroutines.launch

class CartListActivity : BaseActivity(), AudioRecordListener {

    private lateinit var binding: ActivityCartListBinding
    private var permissionsRequired = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var permissionCode = 20
    private var permissionToRecordAccepted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backAction.setOnClickListener {
            finish()
        }

        binding.checkOutAction.setOnClickListener {
            val intent = Intent(this@CartListActivity, CheckOutActivity::class.java)
            intent.putExtra(Constants.ORDER_PRICE, binding.priceAmount.text.toString())
            startActivity(intent)
        }

        binding.addVoiceNotes.setOnClickListener {
            if (letsCheckPermissions()) {
                recordAudio()
            } else {
                ActivityCompat.requestPermissions(this, permissionsRequired, permissionCode)
            }

        }

        binding.cartList.layoutManager = LinearLayoutManager(this)
        binding.recommendedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        CarCareApplication.instance.cartRepository.cartList.observe(this) { cartList ->
            if (cartList != null && cartList.isNotEmpty()) {
                val cartAdapter = CartListAdapter(cartList, object : CartListAdapter.ItemClickListener {
                    override fun deleteCart(id: Int) {
                        CarCareApplication.instance.applicationScope.launch {
                            CarCareApplication.instance.cartRepository.delete(id)
                        }
                    }

                })
                binding.cartList.adapter = cartAdapter

                var totalPrice = 0.0

                for (item in cartList) {
                    totalPrice += if (item.offer > 0L) {
                        item.offer
                    } else {
                        item.price
                    }
                }

                binding.priceAmount.text = "₹ $totalPrice"

            }
        }

        if (RecommendedServiceResponse.getServiceResponse.data.recommended.isNotEmpty()) {
            val serviceListAdapter = ServiceListAdapter(RecommendedServiceResponse.getServiceResponse.data.recommended, object : ServiceListAdapter.ItemClickListener {
                override fun itemClick(data: ServiceData) {
                    val intent = Intent(this@CartListActivity, ServiceDetailsActivity::class.java)
                    intent.putExtra(Constants.SERVICE_ID, data.id)
                    intent.putExtra(Constants.CITY, "Coimbatore")
                    intent.putExtra(Constants.STATE, "TN")
                    startActivity(intent)
                }
            })
            binding.recommendedList.adapter = serviceListAdapter
        }

    }

    override fun onAudioReady(audioUri: String?) {
        Log.e("audioUri -->", "onAudioReady: " + audioUri)
    }

    override fun onReadyForRecord() {
        Log.e("audioUri -->", "onReadyForRecord: called")
    }

    override fun onRecordFailed(errorMessage: String?) {
        showToast(errorMessage!!)
    }

    private fun letsCheckPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {

            permissionToRecordAccepted = if (grantResults.size == 2) {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) && ((grantResults[1] == PackageManager.PERMISSION_GRANTED))
            } else {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
            if (permissionToRecordAccepted) {
                recordAudio()
            }
        }
        if (!permissionToRecordAccepted) Toast.makeText(
            this,
            "You have to accept permissions to send voice",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun recordAudio() {
        val dialog = VoiceSenderDialog(this)
        dialog.setBeepEnabled(true)
        dialog.show(supportFragmentManager, "VOICE")
    }
}