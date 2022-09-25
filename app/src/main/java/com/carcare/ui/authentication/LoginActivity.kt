package com.carcare.ui.authentication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import com.carcare.MainActivity
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.databinding.ActivityLoginBinding
import com.carcare.ui.BaseActivity
import com.carcare.utils.PreferenceHelper
import com.carcare.viewmodel.request.LoginRequestBodies
import java.util.regex.Pattern


class LoginActivity : BaseActivity() {

    val REG = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}\$"
    var PATTERN: Pattern = Pattern.compile(REG)
    private lateinit var viewModel: AuthenticationViewModel
    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        binding.loginContainer.visibility = View.VISIBLE
        binding.otpContainer.visibility = View.GONE
        binding.notYourNumber.setOnClickListener {
            binding.loginContainer.visibility = View.VISIBLE
            binding.otpContainer.visibility = View.GONE
        }
        binding.btnLogin.setOnClickListener {
            if (binding.phoneNumberEdt.text?.isPhoneNumber() == false) {
                showToast(getString(R.string.please_enter_valid_mobile_number))
            } else {
                val body = LoginRequestBodies.GetOTPBody(binding.phoneNumberEdt.text.toString())
                viewModel.getOTP(body)
            }
        }

        binding.termAction.setOnClickListener {
            loadCustomTabs("https://loremipsum.io/privacy-policy/")
        }

        binding.privacyAction.setOnClickListener {
            loadCustomTabs("https://loremipsum.io/privacy-policy/")
        }
        binding.resendAction.setOnClickListener {
            binding.btnLogin.performClick()
        }
        binding.btnSubmit.setOnClickListener {
            if (binding.otpView.otp.isNullOrEmpty()) {
                showToast(getString(R.string.please_enter_valid_otp))
            } else {
                val body = LoginRequestBodies.LoginRequestBody(
                    binding.phoneNumberEdt.text.toString(),
                    binding.otpView.otp
                )
                viewModel.doLogin(body)
            }
        }

        viewModel.getOTPResponse.observe(this) { response ->
            showToast(response.message)
            binding.otpConfirmTxt.text = getString(R.string.please_enter_6_digit_code_sent, binding.phoneNumberEdt.text)
            binding.loginContainer.visibility = View.GONE
            binding.otpContainer.visibility = View.VISIBLE
            binding.otpView.otp = ""

        }

        viewModel.loginResponse.observe(this) { response ->

            if (response.data != null && !response.data.name.isNullOrEmpty()) {
                prefsHelper.intUserIDPref = response.data.userId
                prefsHelper.intUserNamePref = response.data.name
            }
                val fragment =
                    NameUpdateBottomSheetFragment.newInstance(object :
                        NameUpdateBottomSheetFragment.ItemClickListener {
                        override fun onSubmitClick(userName: String) {
                            prefsHelper.intUserNamePref = userName
                            val body = LoginRequestBodies.UpdateNameBody(
                                prefsHelper.intUserIDPref!!,
                                userName
                            )
                            viewModel.updateUserName(body)
                        }
                    })
            fragment.isCancelable = false
                fragment.show(supportFragmentManager, fragment.tag)
//            } else if (response.data != null && !response.data.name.isNullOrEmpty()) {
//                prefsHelper.intUserIDPref = response.data.userId
//                prefsHelper.intUserNamePref = response.data.name
//                val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                showToast(response.message)
//            }
        }

        viewModel.updateUserNameResponse.observe(this) { response ->
//            prefsHelper.intUserIDPref = response.data.userId
//                prefsHelper.intUserNamePref = response.data.name
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
        viewModel.isLoading.observe(this) { isLoading ->
            setDialog(isLoading)
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            showToast(errorMessage.toString())
        }

    }

    fun loadCustomTabs(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    fun CharSequence.isPhoneNumber(): Boolean = PATTERN.matcher(this).find()

}