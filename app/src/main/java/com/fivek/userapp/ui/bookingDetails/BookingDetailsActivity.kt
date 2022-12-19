package com.fivek.userapp.ui.bookingDetails

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivek.userapp.R
import com.fivek.userapp.databinding.ActivityBookingDetailsBinding
import com.fivek.userapp.ui.BaseActivity
import com.fivek.userapp.ui.bookingDetails.adapter.BookingServiceItemAdapter
import com.fivek.userapp.ui.bookingDetails.adapter.JobStatusAdapter
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.viewmodel.request.booking.AddBookingRequest
import com.fivek.userapp.viewmodel.request.booking.FeedbackRequest
import com.fivek.userapp.viewmodel.response.bookingDetails.BookingDetailsResponse
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class BookingDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityBookingDetailsBinding
    private lateinit var bookingDetailsViewModel: BookingDetailsViewModel
    private lateinit var bookingDetailsResponse: BookingDetailsResponse
    var bookingId = ""
    var invoicePath : String?=null

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


        binding.invoiceDownload.setOnClickListener {
            checkStoragePermission(object : PermissionListener{
                override fun permissionGranted() {
                    invoicePath = ""+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Invoice_"+Calendar.getInstance().timeInMillis + ".pdf"
                    val filePath = File(invoicePath)
                    bookingDetailsViewModel.downloadInvoiceResponse(bookingDetailsResponse.data.id,filePath)

                }

                override fun permissionDenied() {

                }

            })
        }

        bookingDetailsViewModel.cancelBookingResponse.observe(this) { response ->
            if (response != null) {
                callBookingDetails()
            }
        }

        bookingDetailsViewModel.invoiceResponse.observe(this) { response ->
            if (response != null) {

                if(response){
                    showToast(getString(R.string.invoice_downloaded_successfully, invoicePath))
                    if(invoicePath !=null) {
                        openDocumentToExternal(invoicePath!!, this@BookingDetailsActivity)
                    }
                }

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


                if(response.data.review.isNullOrEmpty()){
                    binding.writeRatingTxt.text = getString(R.string.write_review)
                }else {
                    binding.writeRatingTxt.text = getString(R.string.edit_review)
                }
                binding.cancelBooking.visibility = View.GONE
//                if (response.data.status.toLowerCase() == "booked") {
//                    binding.cancelBooking.visibility = View.VISIBLE
//                } else {
//                    binding.cancelBooking.visibility = View.GONE
//                }

                if (response.data.status.toLowerCase() == "completed") {
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
        var review = ""
        if(!bookingDetailsResponse.data.review.isNullOrEmpty()){
            review =bookingDetailsResponse.data.review
        }
        val fragment =
            CustomFeedbackProvideSheet.newInstance(binding.ratingCount.rating, review, object :
                CustomFeedbackProvideSheet.ItemClickListener {
                override fun onSubmitClick(rating: Float, feedback: String) {

                    val body = FeedbackRequest.Request(
                        rating,
                        feedback,
                        bookingDetailsResponse.data.serviceIds,
                        bookingDetailsResponse.data.outletId,
                        bookingDetailsResponse.data.id
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

    fun openDocumentToExternal(filePath: String?, context: Context) {
        var intent: Intent
        val file = File(filePath)
        val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
        val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(
                context,
                java.lang.String.format("%s%s", context.packageName, Constants.PROVIDER),
                file
            )
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        } else {
            intent = Intent(Intent.ACTION_VIEW)
            if (extension.equals("", ignoreCase = true) || mimetype == null) {
                // if there is no extension or there is no definite mimetype, still try to open the file
                intent.setDataAndType(Uri.fromFile(file), "application/*")
            } else {
                intent.setDataAndType(Uri.fromFile(file), mimetype)
            }
            intent = Intent.createChooser(intent, "Open File")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, context.getString(R.string.error_message_action_view), Toast.LENGTH_SHORT).show()
            }
        } catch (e: ActivityNotFoundException) {
            // Instruct the user to install a PDF reader here, or something
            Toast.makeText(context, context.getString(R.string.error_message_action_view), Toast.LENGTH_SHORT).show()
        }
    }
}