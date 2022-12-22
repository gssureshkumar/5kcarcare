package com.fivek.userapp.ui.checkout.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.R
import com.fivek.userapp.utils.FontSpan
import com.fivek.userapp.viewmodel.response.outlets.Outlet


class OutletsAdapter(private var items: MutableList<Outlet>, private var itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<OutletsAdapter.ItemViewHolder>() {

    var previousSelectPos = 0
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val radioBtn: RadioButton
        val radioGrp: RadioGroup
        val outletName: TextView
        val eliteView: TextView
        val outletRating: TextView

        init {
            // Define click listener for the ViewHolder's View.
            outletName = itemView.findViewById(R.id.outletName)
            eliteView = itemView.findViewById(R.id.eliteView)
            outletRating = itemView.findViewById(R.id.outletRating)
            radioBtn = itemView.findViewById(R.id.radioBtn)
            radioGrp = itemView.findViewById(R.id.radioGrp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outlet_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(binding: ItemViewHolder, position: Int) {

        val builder = SpannableStringBuilder()
        val word: Spannable = SpannableString(items[position].name)
        word.setSpan(FontSpan(ResourcesCompat.getFont(context, R.font.opensans_regular)), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(word)
        val  distance =items[position].distance/1000
        val wordTwo: Spannable = SpannableString(" ($distance KM)")
        wordTwo.setSpan(FontSpan(ResourcesCompat.getFont(context, R.font.opensans_bold)), 0, wordTwo.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(wordTwo)
        binding.outletName.setText(builder, TextView.BufferType.SPANNABLE);
        binding.outletRating.text = items[position].rating.toString()


        if(items[position].elite){
            binding.eliteView.visibility =View.VISIBLE
        }else{
            binding.eliteView.visibility =View.GONE
        }
        binding.radioBtn.setOnClickListener {

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
        binding.radioGrp.clearCheck()
        if (previousSelectPos == position) {
            binding.radioGrp.check(binding.radioBtn.id)
        }
    }


    interface ItemClickListener {
        fun itemClick(outlet: Outlet)
    }

    fun updateNotify() {
        notifyDataSetChanged()
    }
}