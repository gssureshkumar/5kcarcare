package com.carcare.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.database.VehicleModel
import com.carcare.databinding.VehicleModelItemBinding
import kotlinx.coroutines.launch

class VehicleModelAdapter(private var items: List<VehicleModel>) :
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
        vehicleViewBinding.carModelName.text = items[position].modelName
        vehicleViewBinding.registrationNum.text = items[position].registration
        if (items[position].isPrimary == true) {
            vehicleViewBinding.primaryMode.visibility = View.VISIBLE
        } else {
            vehicleViewBinding.primaryMode.visibility = View.GONE
        }

        vehicleViewBinding.deleteIcon.setOnClickListener {
            CarCareApplication.instance.applicationScope.launch {
                CarCareApplication.instance.repository.delete(items[position])
            }
            notifyItemRemoved(position)
        }
        when (items[position].carModel) {
            context.getString(R.string.hatch_back) -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_hatch_back_icon)
            }
            context.getString(R.string.sedan) -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_sedan_back_icon)
            }
            context.getString(R.string.suv) -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_suv_back_icon)
            }
            context.getString(R.string.muv) -> {
                vehicleViewBinding.carModelIcon.setImageResource(R.drawable.ic_7_rect_seaters_icon)
            }
        }

    }
}