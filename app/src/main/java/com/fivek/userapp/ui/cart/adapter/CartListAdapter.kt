package com.fivek.userapp.ui.cart.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.database.CartModelData
import com.fivek.userapp.databinding.ItemCartViewBinding
import com.fivek.userapp.utils.loadImage
import com.fivek.userapp.utils.loadImageWithCorner
import java.util.*

class CartListAdapter(private var items: List<CartModelData>, private var itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<CartListAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemCartViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemCartViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        binding.serviceName.text = items[position].serviceName

        if(items[position].serviceImg !=null) {
            binding.serviceImage.loadImageWithCorner(items[position].serviceImg!!)
        }else{
            binding.serviceImage.loadImage("")
        }

        binding.priceAmount.visibility = View.GONE
        if(items[position].offer>0L){
            binding.priceAmount.visibility = View.VISIBLE
            binding.offerAmount.text ="₹ "+ items[position].offer
            binding.priceAmount.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                text = "₹ "+ items[position].price
            }
        }else {
            binding.offerAmount.text ="₹ "+ items[position].price
        }

        binding.closeIcon.setOnClickListener {
            itemClickListener.deleteCart(items[position].serviceId)
        }


        if(position == (items.size-1)){
            binding.viewLine.visibility = View.GONE
        }else {
            binding.viewLine.visibility = View.VISIBLE
        }

    }

    interface ItemClickListener {
        fun deleteCart(id: Int)
    }
}