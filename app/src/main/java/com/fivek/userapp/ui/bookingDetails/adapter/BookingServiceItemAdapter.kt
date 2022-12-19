package com.fivek.userapp.ui.bookingDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.databinding.ItemBookingServiceViewBinding
import com.fivek.userapp.utils.loadImageWithCorner
import com.fivek.userapp.viewmodel.response.bookingDetails.Services

class BookingServiceItemAdapter(private var items: List<Services>) :
    RecyclerView.Adapter<BookingServiceItemAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemBookingServiceViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemBookingServiceViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        binding.serviceName.text = items[position].name
        if(items[position].banner.isNullOrEmpty()){
            binding.serviceImage.loadImageWithCorner("")
        }else {
            binding.serviceImage.loadImageWithCorner(items[position].banner)
        }
        binding.priceAmount.visibility = View.GONE
        binding.offerAmount.visibility = View.GONE

        if (position == (items.size - 1)) {
            binding.viewLine.visibility = View.GONE
        } else {
            binding.viewLine.visibility = View.VISIBLE
        }

    }

}