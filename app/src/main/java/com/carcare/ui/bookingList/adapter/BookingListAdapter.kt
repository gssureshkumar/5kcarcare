package com.carcare.ui.bookingList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carcare.database.VehicleModel
import com.carcare.databinding.ItemBookingListBinding
import com.carcare.databinding.ItemOurServicesViewBinding
import com.carcare.ui.home.adapter.ServiceListAdapter

class BookingListAdapter(private var items: List<VehicleModel>) :
    RecyclerView.Adapter<BookingListAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemBookingListBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemBookingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    }

}