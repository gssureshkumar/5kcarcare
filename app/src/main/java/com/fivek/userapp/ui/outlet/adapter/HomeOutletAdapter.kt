package com.fivek.userapp.ui.outlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.databinding.ItemHomeOutletViewBinding
import com.fivek.userapp.viewmodel.response.outlets.Outlet

class HomeOutletAdapter(private var items: List<Outlet>, private var itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<HomeOutletAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemHomeOutletViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemHomeOutletViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        binding.outletName.text = item.name
        binding.outletAddress.text = item.address
        binding.outletNumber.text = item.mobileNumber

        binding.outletNumber.setOnClickListener {
            itemClickListener.itemClick(item)
        }

        binding.outletAddress.setOnClickListener {
            itemClickListener.moveToMap(item)
        }

    }

    interface ItemClickListener{
        fun itemClick(data: Outlet)
        fun moveToMap(data: Outlet)
    }
}