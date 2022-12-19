package com.fivek.userapp.ui.cart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener
import com.fivek.userapp.R
import com.fivek.userapp.app.CarCareApplication
import com.fivek.userapp.databinding.ActivityCartListBinding
import com.fivek.userapp.ui.BaseActivity
import com.fivek.userapp.ui.cart.adapter.CartListAdapter
import com.fivek.userapp.ui.checkout.CheckOutActivity
import com.fivek.userapp.ui.checkout.CheckOutViewModel
import com.fivek.userapp.ui.home.RecommendedServiceResponse
import com.fivek.userapp.ui.home.adapter.OtherServicesAdapter
import com.fivek.userapp.ui.serviceDetails.ServiceDetailsActivity
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.utils.UploadUtility
import com.fivek.userapp.viewmodel.response.services.ServiceData
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

class CartListActivity : BaseActivity(), AudioRecordListener {

    private lateinit var checkOutViewModel: CheckOutViewModel
    private lateinit var binding: ActivityCartListBinding
    private var permissionsRequired = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var permissionCode = 20
    private var permissionToRecordAccepted = false
    private var vehicleType = ""
    private var recordAudioPath: String? = null
    private var mediaPlayer: MediaPlayer? = null
    var audioId: String? = null
    var isElite = false
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkOutViewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        binding.backAction.setOnClickListener {
            finish()
        }

        binding.checkOutAction.setOnClickListener {
            val intent = Intent(this@CartListActivity, CheckOutActivity::class.java)
            intent.putExtra(Constants.ORDER_PRICE, binding.priceAmount.text.toString())
            intent.putExtra(Constants.AUDIO_ID, audioId)
            intent.putExtra(Constants.IS_ELITE, isElite)
            startActivity(intent)
        }

        binding.addVoiceNotes.setOnClickListener {
            if (letsCheckPermissions()) {
                recordAudio()
            } else {
                ActivityCompat.requestPermissions(this, permissionsRequired, permissionCode)
            }

        }

        binding.deleteIcon.setOnClickListener {

            val builder = AlertDialog.Builder(this@CartListActivity)
            builder.setMessage(getString(R.string.do_you_want_to_delete_audio_instructions))
            builder.setPositiveButton(R.string.yes) { dialog, which ->
                binding.playVoiceNotes.visibility = View.GONE
                binding.addVoiceNotes.visibility = View.VISIBLE
            }

            builder.setNegativeButton(R.string.no) { dialog, which ->

            }

            builder.show()

        }

        binding.playIcon.setOnClickListener {
            if (mediaPlayer == null) {
                binding.playIcon.setBackgroundResource(R.drawable.ic_pause_circle_icon)
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(recordAudioPath)
                    setOnCompletionListener {
                        pauseMediaPlayer()
                    }
                    prepare()
                    start()
                }
            } else {
                pauseMediaPlayer()
            }
        }

        binding.cartList.layoutManager = LinearLayoutManager(this)
        binding.recommendedList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        CarCareApplication.instance.repository.primaryVehicle.observe(this) { vehile ->
            if (vehile?.type != null) {
                vehicleType = vehile.type
            }
        }
        CarCareApplication.instance.cartRepository.cartList.observe(this) { cartList ->
            if (cartList != null && cartList.isNotEmpty()) {
                val cartAdapter =
                    CartListAdapter(cartList, object : CartListAdapter.ItemClickListener {
                        override fun deleteCart(id: Int) {
                            CarCareApplication.instance.applicationScope.launch {
                                CarCareApplication.instance.cartRepository.delete(id)
                            }
                        }

                    })
                binding.cartList.adapter = cartAdapter

                var totalPrice = 0.0

                for (item in cartList) {
                    if (item.elite == true) {
                        isElite = true
                    }
                    totalPrice += if (item.offer > 0L) {
                        item.offer
                    } else {
                        item.price
                    }
                }

                binding.priceAmount.text = "₹ ${totalPrice.roundToInt()}"

            } else {
                finish()
            }
        }

        if (RecommendedServiceResponse.getServiceResponse.data.recommended.isNotEmpty()) {
            val serviceListAdapter = OtherServicesAdapter(
                RecommendedServiceResponse.getServiceResponse.data.recommended,
                object : OtherServicesAdapter.ItemClickListener {
                    override fun itemClick(data: ServiceData) {
                        val intent =
                            Intent(this@CartListActivity, ServiceDetailsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        intent.putExtra(Constants.SERVICE_ID, data.id)
                        intent.putExtra(
                            Constants.CITY,
                            CarCareApplication.instance.locationInfoData.city
                        )
                        intent.putExtra(Constants.STATE, "TN")
                        intent.putExtra(Constants.VEHICLE_TYPE, vehicleType)
                        startActivity(intent)
                    }
                })
            binding.recommendedList.adapter = serviceListAdapter
        }
        checkOutViewModel.signedUrlResponse.observe(this) { response ->
            if (response != null) {
                UploadUtility(this@CartListActivity).uploadFile(
                    File(recordAudioPath),
                    response.data.url,
                    object : UploadUtility.UploadListener {
                        override fun uploaded() {
                            runOnUiThread {
                                binding.playVoiceNotes.visibility = View.VISIBLE
                                binding.addVoiceNotes.visibility = View.GONE
                                audioId = response.data.objectId
                            }

                        }

                        override fun failed() {

                        }

                    })
            }
        }

        checkOutViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        checkOutViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }

    }

    override fun onAudioReady(audioUri: String?) {
        recordAudioPath = audioUri

        val hashMap = HashMap<String, Any>()
        hashMap["mediaType"] = "audio"
        hashMap["urlType"] = "1"
        checkOutViewModel.getSignedUrl(hashMap)

    }

    fun pauseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
            mediaPlayer!!.release()
            binding.playIcon.setBackgroundResource(R.drawable.ic_play_circle_icon)
            mediaPlayer = null
        }
    }

    override fun onPause() {
        super.onPause()

        pauseMediaPlayer()

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {

            permissionToRecordAccepted = if (grantResults.size == 2) {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) && ((grantResults[1] == PackageManager.PERMISSION_GRANTED))
            } else {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
            if (permissionToRecordAccepted) {
                recordAudio()
            } else {
                Toast.makeText(
                    this,
                    "You have to accept permissions to send voice",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun recordAudio() {
        val dialog = VoiceSenderDialog(this)
        dialog.setBeepEnabled(true)
        dialog.show(supportFragmentManager, "VOICE")
    }
}