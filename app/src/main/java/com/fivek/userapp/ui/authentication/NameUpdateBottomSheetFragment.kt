package com.fivek.userapp.ui.authentication

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fivek.userapp.R
import com.fivek.userapp.databinding.BottomSheetNameUpdateBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NameUpdateBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var  itemClickListener : ItemClickListener
    lateinit var binding: BottomSheetNameUpdateBinding
    companion object {
        fun newInstance(itemListener : ItemClickListener): NameUpdateBottomSheetFragment =
            NameUpdateBottomSheetFragment().apply {
                itemClickListener = itemListener
            }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetNameUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            if(binding.userNameEdt.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), getString(R.string.please_enter_valid_user_name), Toast.LENGTH_SHORT).show()
            }else{
                itemClickListener.onSubmitClick(binding.userNameEdt.text.toString())
                dismiss()
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    interface ItemClickListener {
        fun onSubmitClick(userName: String)
    }
}