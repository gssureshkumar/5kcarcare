package com.carcare.ui.bookingList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carcare.databinding.ItemBookingListBinding
import com.carcare.utils.loadImage
import com.carcare.viewmodel.response.bookingList.Bookings
import com.carcare.viewmodel.response.bookingList.Services
import java.text.SimpleDateFormat
import java.util.*

class BookingListAdapter(private var items: List<Bookings>) :
    RecyclerView.Adapter<BookingListAdapter.ItemViewHolder>() {

    private var services: MutableList<Services> = mutableListOf()

    private lateinit var binding: ItemBookingListBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemBookingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = items[position]
        if (item.serviceIds.isNotEmpty()) {
            val service = getService(item.serviceIds[0])
            service?.let {
                binding.bookingImage.loadImage(it.thumbnail)
                binding.serviceName.text = service.name
            }
        } else {
            binding.bookingImage.loadImage("")
            binding.serviceName.text = "No Service"
        }
        binding.localAddress.text = item.name
        updateDate(item.bookingDate, binding.bookedDate)
        binding.bookingStatus.text = item.status
        binding.bookingPriceTxt.text = "₹ " + item.finalPrice

    }


    fun updateDate(dateStr: String, bookingDate: TextView) {
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            val mDate = formatter.parse(dateStr)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            bookingDate.text = format.format(mDate)
        } catch (e: Exception) {
        }
    }

    fun updateService(services: MutableList<Services>) {
        this.services = services
    }

    fun getService(serviceId: Int): Services? {
        return services.find { service -> service.id == serviceId }

    }

}