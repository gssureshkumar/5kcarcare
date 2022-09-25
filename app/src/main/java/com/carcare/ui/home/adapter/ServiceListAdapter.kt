package com.carcare.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carcare.database.VehicleModel
import com.carcare.databinding.ItemOurServicesViewBinding

class ServiceListAdapter(private var items: List<VehicleModel>) :
    RecyclerView.Adapter<ServiceListAdapter.ItemViewHolder>() {


    private lateinit var servicesViewBinding: ItemOurServicesViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        servicesViewBinding = ItemOurServicesViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(servicesViewBinding.root)
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    }

}