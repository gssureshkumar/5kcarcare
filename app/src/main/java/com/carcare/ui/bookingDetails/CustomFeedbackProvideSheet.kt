package com.carcare.ui.bookingDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.carcare.R
import com.carcare.databinding.BottomFeedbackViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomFeedbackProvideSheet : BottomSheetDialogFragment() {

    lateinit var itemClickListener: ItemClickListener
    lateinit var binding: BottomFeedbackViewBinding
    var type: String = ""

    companion object {
        fun newInstance(itemListener: ItemClickListener): CustomFeedbackProvideSheet =
            CustomFeedbackProvideSheet().apply {
                itemClickListener = itemListener
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomFeedbackViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeView.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {

            if(binding.etReviewBox.text.isEmpty()){
                Toast.makeText(requireContext(), context?.getString(R.string.please_enter_feedback), Toast.LENGTH_SHORT).show()
            }else {
                dismiss()
                itemClickListener.onSubmitClick(binding.ratingCount.rating, binding.etReviewBox.text.toString())
            }
        }


    }

    interface ItemClickListener {
        fun onSubmitClick(rating: Float, feedback :String)
    }

}