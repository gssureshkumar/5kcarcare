package com.carcare.ui.serviceDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carcare.databinding.ItemOurServicesViewBinding
import com.carcare.databinding.ItemServiceIncludeViewBinding
import com.carcare.ui.home.adapter.ServiceListAdapter
import com.carcare.utils.loadImage
import com.carcare.viewmodel.response.services.ServiceData

class ServiceIncludedAdapter(private var items: List<String>) :
    RecyclerView.Adapter<ServiceIncludedAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemServiceIncludeViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemServiceIncludeViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        binding.serviceTxt.text = items[position]
    }

}