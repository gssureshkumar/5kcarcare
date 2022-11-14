package com.carcare.ui.bookingDetails

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcare.R
import com.carcare.databinding.ActivityBookingDetailsBinding
import com.carcare.ui.BaseActivity
import com.carcare.ui.bookingDetails.adapter.BookingServiceItemAdapter
import com.carcare.ui.bookingDetails.adapter.JobStatusAdapter
import com.carcare.utils.Constants
import com.carcare.viewmodel.request.booking.AddBookingRequest
import com.carcare.viewmodel.request.booking.FeedbackRequest
import com.carcare.viewmodel.response.bookingDetails.BookingDetailsResponse
import kotlin.math.roundToInt

class BookingDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityBookingDetailsBinding
    private lateinit var bookingDetailsViewModel: BookingDetailsViewModel
    private lateinit var bookingDetailsResponse: BookingDetailsResponse
    var bookingId = ""

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        finish()
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookingId = intent.extras?.getString(Constants.BOOKING_ID).toString()

        bookingDetailsViewModel = ViewModelProvider(this)[BookingDetailsViewModel::class.java]
        binding.backAction.setOnClickListener {
            finish()
        }

        binding.cancelBooking.setOnClickListener {
            cancelBooking()
        }
        binding.writeRatingTxt.setOnClickListener {
            showFeedBackDialog()
        }

        binding.ratingCount.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { p0, p1, fromUser ->
            if (fromUser) {
                binding.writeRatingTxt.performClick()
            }
        }

        binding.servicesList.layoutManager = LinearLayoutManager(this)
        binding.jobStatusList.layoutManager = LinearLayoutManager(this)

        bookingDetailsViewModel.feedbackResponse.observe(this) { response ->
            if (response != null) {
                callBookingDetails()
            }
        }

        bookingDetailsViewModel.cancelBookingResponse.observe(this) { response ->
            if (response != null) {
                callBookingDetails()
            }
        }

        bookingDetailsViewModel.bookingDetailsResponse.observe(this) { response ->
            if (response != null) {
                bookingDetailsResponse = response
                binding.bookingIdTxt.text = getString(R.string.booking_id, response.data.id)

                if (response.data.services.isNotEmpty()) {
                    val servicesAdapter = BookingServiceItemAdapter(response.data.services)
                    binding.servicesList.adapter = servicesAdapter
                }
                if (response.data.bookingStatus.isNotEmpty()) {
                    val jobStatusAdapter = JobStatusAdapter(response.data.bookingStatus)
                    binding.jobStatusList.adapter = jobStatusAdapter
                }

                binding.actualPrice.text = "₹ ${response.data.actual.roundToInt()}"
                binding.offerPrice.text = "₹ ${response.data.offer.roundToInt()}"
                var totalOffer = response.data.offer.roundToInt()

                if (response.data.voucherCode.isNotEmpty()) {
                    totalOffer -= response.data.voucher.roundToInt()
                    binding.voucherAppliedView.visibility = View.VISIBLE
                    binding.voucherTotal.text = "₹ -${response.data.voucher.roundToInt()}"
                    binding.voucherCode.text = response.data.voucherCode
                } else {
                    binding.voucherAppliedView.visibility = View.GONE
                }

                if (response.data.membership > 0) {
                    totalOffer -= response.data.membership.roundToInt()
                    binding.membershipView.visibility = View.VISIBLE
                    binding.membershipTotal.text = "₹ -${response.data.membership.roundToInt()}"
                } else {
                    binding.membershipView.visibility = View.GONE
                }

                binding.finalPrice.text = "₹ $totalOffer"
                binding.totalPrice.text = "₹ $totalOffer"

                if (response.data.paymentMethod == "online") {
                    binding.paymentType.text = getString(R.string.online)
                } else {
                    binding.paymentType.text = getString(R.string.cash)
                }

                if (response.data.status == "Booked") {
                    binding.cancelBooking.visibility = View.VISIBLE
                } else {
                    binding.cancelBooking.visibility = View.GONE
                }

                if (response.data.status == "Completed") {
                    binding.feedBackContainer.visibility = View.VISIBLE
                } else {
                    binding.feedBackContainer.visibility = View.GONE
                }
                binding.ratingCount.rating = response.data.rating
            }
        }
        bookingDetailsViewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        bookingDetailsViewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }

        callBookingDetails()
    }

    fun callBookingDetails() {
        val query = HashMap<String, Any>()
        query["id"] = bookingId!!
        bookingDetailsViewModel.fetchBookingDetailsResponse(query)
    }

    fun showFeedBackDialog() {
        val fragment = CustomFeedbackProvideSheet.newInstance(object :
            CustomFeedbackProvideSheet.ItemClickListener {
            override fun onSubmitClick(rating: Float, feedback: String) {

                val body = FeedbackRequest.Request(
                    rating, feedback,
                    bookingDetailsResponse.data.serviceIds, bookingDetailsResponse.data.outletId, bookingDetailsResponse.data.id
                )
                bookingDetailsViewModel.updateFeedback(body)
            }

        })
        fragment.isCancelable = false
        fragment.show(supportFragmentManager, fragment.tag)
    }

    fun cancelBooking() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.are_you_sure_you_want_you_cancel_booking))
        builder.setPositiveButton(R.string.yes) { dialog, which ->
            val body = AddBookingRequest.CancelBooking(bookingDetailsResponse.data.id)
            bookingDetailsViewModel.cancelBooking(body)
        }

        builder.setNegativeButton(R.string.no) { dialog, which ->

        }

        builder.show()
    }
}