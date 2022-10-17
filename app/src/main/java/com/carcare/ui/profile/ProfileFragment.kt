package com.carcare.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carcare.BuildConfig
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.databinding.FragmentProfileBinding
import com.carcare.ui.authentication.LoginActivity
import com.carcare.ui.splash.SplashActivity
import com.carcare.utils.PreferenceHelper

class ProfileFragment : Fragment() {

    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
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

        _binding.userNameTxt.text = prefsHelper.intUserNamePref
        _binding.appVersionTxt.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        _binding.mobileNumberTxt.text = prefsHelper.intMobileNumberPref
        _binding.logoutTxt.setOnClickListener {
            prefsHelper.logout()
            prefsHelper.intTutorialPref = true
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}