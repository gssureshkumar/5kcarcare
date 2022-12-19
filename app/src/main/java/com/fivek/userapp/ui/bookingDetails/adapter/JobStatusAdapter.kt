package com.fivek.userapp.ui.bookingDetails.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.R
import com.fivek.userapp.databinding.ItemJobStatusViewBinding
import com.fivek.userapp.viewmodel.response.bookingDetails.BookingStatus
import java.text.SimpleDateFormat
import java.util.*

class JobStatusAdapter(private var items: List<BookingStatus>) :
    RecyclerView.Adapter<JobStatusAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemJobStatusViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemJobStatusViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        binding.jobStatusTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_green_circle_icon, 0,0,0)
        when (items[position].status.lowercase()) {
            "booked" -> {
                binding.jobStatusTxt.text = context.getString(R.string.service_booked)
                binding.jobBookingStatus.text = context.getString(R.string.driver_for_pickup)
            }
            "inoutlet" -> {
                binding.jobStatusTxt.text = context.getString(R.string.in_service_outlet)
            }
            "completed" -> {
                binding.jobStatusTxt.text = context.getString(R.string.completed)
                binding.jobBookingStatus.text = context.getString(R.string.driver_for_drop)
            }
            "cancelled" -> {
                binding.jobStatusTxt.text = context.getString(R.string.cancelled)
                binding.jobStatusTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_red_circle_icon, 0,0,0)
            }
        }

        updateDate(items[position].date, binding.jobDate)

        if ( items[position].name.isNullOrEmpty()) {
            binding.driverView.visibility = View.GONE
            binding.jobBookingStatus.visibility = View.GONE
        } else {
            binding.driverView.visibility = View.VISIBLE
            binding.jobBookingStatus.visibility = View.VISIBLE
            binding.serviceUserName.text = items[position].name
            binding.serviceUserNumber.text = items[position].mobile
            binding.serviceUserNumber.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${items[position].mobile}"))
                context.startActivity(intent)
            }
        }



    }

    fun updateDate(dateStr: String, bookingDate: TextView) {
        try {

            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val mDate = formatter.parse(dateStr)
            val format = SimpleDateFormat("dd MMM yyyy hh:mm a",Locale.getDefault())
            bookingDate.text = "@" + format.format(mDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}