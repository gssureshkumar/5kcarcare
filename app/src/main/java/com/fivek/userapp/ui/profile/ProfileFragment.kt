package com.fivek.userapp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fivek.userapp.BuildConfig
import com.fivek.userapp.MainActivity
import com.fivek.userapp.R
import com.fivek.userapp.app.CarCareApplication
import com.fivek.userapp.databinding.FragmentProfileBinding
import com.fivek.userapp.ui.authentication.LoginActivity
import com.fivek.userapp.ui.checkout.VouchersActivity
import com.fivek.userapp.ui.home.HomeViewModel
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.utils.PreferenceHelper
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var _binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding.userNameTxt.text = prefsHelper.intUserNamePref
        _binding.appVersionTxt.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        _binding.mobileNumberTxt.text = prefsHelper.intMobileNumberPref
        _binding.logoutTxt.setOnClickListener {



            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(getString(R.string.are_you_sure_you_want_you_logout))
            builder.setPositiveButton(R.string.yes) { dialog, which ->
                homeViewModel.userLogout()
            }

            builder.setNegativeButton(R.string.no) { dialog, which ->

            }

            builder.show()
        }

        _binding.bookingListTxt.setOnClickListener {
            (activity as? MainActivity)?.moveToNext(R.id.navigation_booking)
        }
        _binding.contactUsTxt.setOnClickListener {
            (activity as? MainActivity)?.moveToNext(R.id.navigation_contacts)
        }

        _binding.vouchersTxt.setOnClickListener {
            val intent = Intent(requireActivity(), VouchersActivity::class.java)
            startActivity(intent)
        }

        _binding.shareTxt.setOnClickListener {
            (activity as MainActivity).shareToOtherApp()
        }

        _binding.myVehicleTxt.setOnClickListener {
            (activity as? MainActivity)?.showMyVehicle()
        }

        _binding.aboutUsTxt.setOnClickListener {
            loadCustomTabs(Constants.ABOUT_US)
        }

        homeViewModel.userTypeResponse.observe(requireActivity()) { response ->

            if (response != null) {
                prefsHelper.intUserTypePref = response.data.type
                updateUserType()
            }

        }

        homeViewModel.userLogoutResponse.observe(requireActivity()) { response ->

            if (response != null) {
                val deleteJob = CarCareApplication.instance.applicationScope.launch {
                    prefsHelper.logout()
                    CarCareApplication.instance.careDatabase.clearAllTables()
                }

                deleteJob.invokeOnCompletion {
                    (activity as? MainActivity)?.setDialog(false)
                    prefsHelper.intTutorialPref = true
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

            }


        }


        homeViewModel.isLoading.observe(requireActivity()) { isLoading ->
            (activity as? MainActivity)?.setDialog(isLoading)
        }
        homeViewModel.errorMessage.observe(requireActivity()) { errorMessage ->
            (activity as? MainActivity)?.showToast(errorMessage.toString())
        }

        homeViewModel.fetchUserType()

    }

    fun loadCustomTabs(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireActivity(), Uri.parse(url))
    }

    fun updateUserType() {

        if (prefsHelper.intUserTypePref!!.toLowerCase() == "normal") {
            _binding.primaryMode.visibility = View.GONE
        } else {
            _binding.primaryMode.visibility = View.VISIBLE
        }
    }

}