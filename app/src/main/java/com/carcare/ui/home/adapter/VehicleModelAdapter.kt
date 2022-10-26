package com.carcare.ui.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carcare.R
import com.carcare.database.VehicleModel
import com.carcare.databinding.VehicleModelItemBinding
import java.util.*

class VehicleModelAdapter(private var items: List<VehicleModel>, private var itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<VehicleModelAdapter.ItemViewHolder>() {


    private lateinit var vehicleViewBinding: VehicleModelItemBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        vehicleViewBinding = VehicleModelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(vehicleViewBinding.root)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        vehicleViewBinding.vehicleView.setOnClickListener {
            itemClickListener.onItemClick(items[position])
        }
        vehicleViewBinding.carModelName.text = items[position].carModel
        vehicleViewBinding.registrationNum.text = items[position].registration
        if (items[position].isPrimary == true) {
            vehicleViewBinding.primaryMode.visibility = View.VISIBLE
        } else {
            vehicleViewBinding.primaryMode.visibility = View.GONE
        }

        vehicleViewBinding.deleteIcon.setOnClickListener {

            itemClickListener.deleteVehicle(items[position].id)
        }

        when {
            items[position].type!!.lowercase(Locale.getDefault()).contains("hatch") -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_hatch_back_icon)
            }
            items[position].type!!.lowercase(Locale.getDefault()).contains("sedan") -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_sedan_back_icon)
            }
            items[position].type!!.lowercase(Locale.getDefault()).contains(context.getString(R.string.suv).lowercase(Locale.getDefault())) -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_suv_back_icon)
            }
            items[position].type!!.lowercase(Locale.getDefault()).contains("7Seater") -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_7_rect_seaters_icon)
            }
        }


    }

    interface ItemClickListener{
        fun deleteVehicle(id:String)
        fun onItemClick(vehicleModel: VehicleModel)
    }


}