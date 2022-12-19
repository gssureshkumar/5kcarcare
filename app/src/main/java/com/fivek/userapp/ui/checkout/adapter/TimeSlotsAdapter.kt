package com.fivek.userapp.ui.checkout.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.R
import com.fivek.userapp.viewmodel.response.timeslot.Slots

class TimeSlotsAdapter(private var items: MutableList<Slots>, private var itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<TimeSlotsAdapter.ItemViewHolder>() {

    private lateinit var context: Context
    var previousSelectPos = -1

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val date: TextView
        val timeSlot: LinearLayout

        init {
            date = itemView.findViewById(R.id.date)
            timeSlot = itemView.findViewById(R.id.timeSlot)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(binding: ItemViewHolder, position: Int) {

        binding.date.text = items[position].duration
        binding.timeSlot.setOnClickListener {

            if (previousSelectPos > -1) {
                val element = items[previousSelectPos]
                element.selected = false
                items[previousSelectPos] = element
                notifyItemChanged(previousSelectPos)
            }

            previousSelectPos = position
            val item = items[position]
            item.selected = true
            items[position] = item
            notifyItemChanged(position)
            itemClickListener.itemClick(item)


        }

        if(items[position].selected){
            binding.timeSlot.setBackgroundResource(R.drawable.draw_date_light_blue_fill_view)
            binding.date.setTextColor(Color.parseColor("#726EDC"))
        }else {
            binding.timeSlot.setBackgroundResource(R.drawable.draw_rounded_gray_view)
            binding.date.setTextColor(Color.parseColor("#828282"))
        }

    }


    interface ItemClickListener {
        fun itemClick(slot: Slots)
    }
}