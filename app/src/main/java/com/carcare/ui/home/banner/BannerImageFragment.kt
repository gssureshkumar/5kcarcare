package com.carcare.ui.home.banner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carcare.databinding.FragmentBannerBinding
import com.carcare.utils.TutorialDataManager
import com.carcare.utils.loadImage

class BannerImageFragment: Fragment() {

    lateinit var binding: FragmentBannerBinding
    companion object {
        const val ARG_POSITION = "position"

        fun getInstance(position: Int): Fragment {
            val fragment = BannerImageFragment()
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
        binding = FragmentBannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)
        if (TutorialDataManager.getBannerImage().size > position) {
            binding.slidingImage.loadImage(TutorialDataManager.getBannerImage()[position])
        }
    }
}