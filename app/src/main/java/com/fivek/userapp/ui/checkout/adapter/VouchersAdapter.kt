package com.fivek.userapp.ui.checkout.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.databinding.ItemVoucherViewBinding
import com.fivek.userapp.viewmodel.response.vouchers.Voucher

class VouchersAdapter(private var items: List<Voucher>, private var itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<VouchersAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemVoucherViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemVoucherViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        binding.voucherPrice.text = "₹ ${items[position].value}"
        binding.voucherCode.text = items[position].id
        binding.voucherType.text = items[position].type
        binding.voucherView.setOnClickListener {
            itemClickListener.itemClick(items[position])
        }

        if(position == items.size-1){
            binding.viewLine.visibility =View.GONE
        }else{
            binding.viewLine.visibility =View.VISIBLE
        }
    }


    interface ItemClickListener {
        fun itemClick(data: Voucher)
    }

}