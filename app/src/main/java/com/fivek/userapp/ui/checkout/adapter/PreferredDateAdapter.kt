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
import com.fivek.userapp.viewmodel.response.preferredDate.PreferredDate

class PreferredDateAdapter(private var items: MutableList<PreferredDate>, private var itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<PreferredDateAdapter.ItemViewHolder>() {

    var previousSelectPos = 0
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val date: TextView
        val dateTxt: TextView
        val preferredDateView: LinearLayout

        init {
            // Define click listener for the ViewHolder's View.
            date = itemView.findViewById(R.id.date)
            dateTxt = itemView.findViewById(R.id.dateTxt)
            preferredDateView = itemView.findViewById(R.id.preferredDateView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preferred_date_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(binding: ItemViewHolder, position: Int) {

        binding.date.text = items[position].date
        binding.dateTxt.text = items[position].dateName
        binding.preferredDateView.setOnClickListener {
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

        if (items[position].selected) {
            binding.preferredDateView.setBackgroundResource(R.drawable.draw_date_light_blue_fill_view)
            binding.date.setTextColor(Color.parseColor("#726EDC"))
            binding.dateTxt.setTextColor(Color.parseColor("#726EDC"))
        } else {
            binding.preferredDateView.setBackgroundResource(R.drawable.draw_rounded_gray_view)
            binding.date.setTextColor(Color.parseColor("#828282"))
            binding.dateTxt.setTextColor(Color.parseColor("#828282"))
        }
    }

    interface ItemClickListener {
        fun itemClick(slot: PreferredDate)
    }
}