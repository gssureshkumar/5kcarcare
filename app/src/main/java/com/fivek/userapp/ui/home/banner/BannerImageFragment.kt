package com.fivek.userapp.ui.home.banner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fivek.userapp.databinding.FragmentBannerBinding
import com.fivek.userapp.utils.TutorialDataManager
import com.fivek.userapp.utils.loadImageFitXY

class BannerImageFragment: Fragment() {

    lateinit var binding: FragmentBannerBinding
    companion object {
        const val ARG_POSITION = "position"
        lateinit var bannerCallBack: BannerCallBack

        fun getInstance(position: Int, callBack: BannerCallBack): Fragment {
            bannerCallBack = callBack
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
        if (TutorialDataManager.getOffersList.size > position) {
            binding.slidingImage.loadImageFitXY(TutorialDataManager.getOffersList[position].banner)
        }
        binding.slidingImage.setOnClickListener {
            bannerCallBack.bannerClick(TutorialDataManager.getOffersList[position].id)

        }
    }
}