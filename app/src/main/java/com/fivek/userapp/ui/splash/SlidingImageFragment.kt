package com.fivek.userapp.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fivek.userapp.databinding.FragmentSlidingBinding
import com.fivek.userapp.utils.TutorialDataManager

class SlidingImageFragment : Fragment() {

    private var binding : FragmentSlidingBinding? =null

    companion object {
        const val ARG_POSITION = "position"

        fun getInstance(position: Int): Fragment {
            val fragment = SlidingImageFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_POSITION, position)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlidingBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)
        val data =  TutorialDataManager.getTutorialData(position)
        binding!!.titleTxt.text = data.title
        binding!!.descriptionTxt.text = data.description
        binding!!.slidingImage.setImageResource(data.icon)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}