package com.fivek.userapp.ui.serviceDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivek.userapp.databinding.ItemRatingViewBinding
import com.fivek.userapp.viewmodel.response.servicesDetailsResponse.Reviews

class RatingListAdapter(private var items: List<Reviews>) :
    RecyclerView.Adapter<RatingListAdapter.ItemViewHolder>() {


    private lateinit var binding: ItemRatingViewBinding
    private lateinit var context: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        binding = ItemRatingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        binding.commentsTxt.text = items[position].review
        binding.ratingCount.rating = items[position].rating

        if(items[position].name.isNullOrEmpty()) {
            binding.raterName.visibility= View.GONE
            binding.profileTxt.visibility= View.GONE
        }else {
            binding.raterName.visibility= View.VISIBLE
            binding.profileTxt.visibility= View.VISIBLE
            binding.raterName.text = items[position].name
            val firstLetter = items[position].name.substring(0, 1).toUpperCase()
            binding.profileTxt.text = firstLetter
        }
    }

}