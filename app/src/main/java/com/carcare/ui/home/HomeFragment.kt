package com.carcare.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.carcare.MainActivity
import com.carcare.R
import com.carcare.databinding.FragmentHomeBinding
import com.carcare.ui.home.banner.BannerImagesAdapter
import com.carcare.ui.setaddress.MapsActivity
import com.carcare.utils.Constants
import com.carcare.utils.TutorialDataManager
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    private var currentPage = 0
    private lateinit var slidingImageDots: Array<ImageView?>
    private var slidingDotsCount = 0
    private var handler = Handler()
    private var timer = Timer()

    private val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            for (i in 0 until slidingDotsCount) {
                slidingImageDots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.non_active_dot
                    )
                )
            }

            slidingImageDots[position]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSlidingViewPager()

        val address = (activity as MainActivity).getCurrentAddress()
       updateAddess(address)

        _binding.localAddress.setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            resultLauncher.launch(intent)
        }
    }


    private fun setUpSlidingViewPager() {
        timer = Timer()
        handler = Handler()
        slidingDotsCount = TutorialDataManager.getBannerImage().size

        val landingImagesAdapter =
            BannerImagesAdapter(requireActivity() as AppCompatActivity, slidingDotsCount)
        _binding.slidingViewPager.apply {
            adapter = landingImagesAdapter
            registerOnPageChangeCallback(slidingCallback)
        }


        slidingImageDots = arrayOfNulls(slidingDotsCount)

        _binding.sliderDots.removeAllViews()
        for (i in 0 until slidingDotsCount) {
            slidingImageDots[i] = ImageView(requireContext())
            slidingImageDots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.non_active_dot
                )
            )
            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(8, 0, 8, 0)
            _binding.sliderDots.addView(slidingImageDots[i], params)
        }

        slidingImageDots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )


        val update = Runnable {
            if (currentPage == slidingDotsCount) {
                currentPage = 0
            }
            try {
                _binding.slidingViewPager.setCurrentItem(currentPage++, true)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3500, 3500)


    }

    fun updateAddess(address :String){
        _binding.localAddress.visibility = View.INVISIBLE
        _binding.loadingView.visibility = View.VISIBLE
        if(address.isNotEmpty()) {
            _binding.loadingView.visibility = View.GONE
            _binding.localAddress.visibility = View.VISIBLE
            _binding.localAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pickup_pin_icon,0,0,0)
            _binding.localAddress.text =address
        }

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if(data !=null) {
               val address = data.getStringExtra(Constants.NEW_ADDRESS_UPDATE)
                address?.let { updateAddess(it) }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}