package com.carcare.ui.authentication

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import com.carcare.BuildConfig
import com.carcare.MainActivity
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.database.VehicleModel
import com.carcare.databinding.ActivityLoginBinding
import com.carcare.ui.BaseActivity
import com.carcare.utils.Constants
import com.carcare.utils.PreferenceHelper
import com.carcare.viewmodel.request.LoginRequestBodies
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class LoginActivity : BaseActivity() {

    val REG = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}\$"
    var PATTERN: Pattern = Pattern.compile(REG)
    private lateinit var viewModel: AuthenticationViewModel
    private var userName = ""
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

        binding.phoneNumberEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btnLogin.isEnabled = p0.toString().length == 10
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.termAction.setOnClickListener {
            loadCustomTabs(Constants.TERMS_CONDITION)
        }

        binding.privacyAction.setOnClickListener {
            loadCustomTabs(Constants.PRIVACY_POLICY)
        }
        binding.resendAction.setOnClickListener {
            binding.btnLogin.performClick()
        }

        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.btnSubmit.isEnabled = false
            }

            override fun onOTPComplete(otp: String?) {
                binding.btnSubmit.isEnabled = true
            }

        }
        binding.btnSubmit.setOnClickListener {
            if (binding.otpView.otp.isNullOrEmpty()) {
                showToast(getString(R.string.please_enter_valid_otp))
            } else {
                val body = LoginRequestBodies.LoginRequestBody(
                    binding.phoneNumberEdt.text.toString(),
                    binding.otpView.otp,
                    prefsHelper.intUserReferPref
                )
                viewModel.doLogin(body)
            }
        }

        viewModel.getOTPResponse.observe(this) { response ->
            binding.otpConfirmTxt.text = getString(R.string.please_enter_6_digit_code_sent, binding.phoneNumberEdt.text)
            binding.loginContainer.visibility = View.GONE
            binding.otpContainer.visibility = View.VISIBLE
            binding.otpView.otp = ""

        }

        viewModel.loginResponse.observe(this) { response ->

            if (response.data != null) {
                prefsHelper.intUserIDPref = response.data.id
                prefsHelper.intUserNamePref = response.data.name
                prefsHelper.intMobileNumberPref = binding.phoneNumberEdt.text.toString()
                prefsHelper.intAuthTokenPref = response.data.accessToken
                prefsHelper.intRefreshTokenPref = response.data.refreshToken
                prefsHelper.intUserTypePref = response.data.type
                prefsHelper.intRefCodePref = response.data.refCode
                if (response.data.vehicles.isNotEmpty()) {
                    val listOfVehicle = mutableListOf<VehicleModel>()
                    for (data in response.data.vehicles) {
                        val vehicle = VehicleModel(data.id, data.type, data.primary, data.model, data.reg_no)
                        listOfVehicle.add(vehicle)
                    }
                    CarCareApplication.instance.applicationScope.launch {
                        CarCareApplication.instance.repository.insert(listOfVehicle)
                    }
                }

                val link = "https://carcare.page.link/finally?invitedby=" + prefsHelper.intRefCodePref
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(link))
                    .setDomainUriPrefix("https://carcare.page.link")
                    .setAndroidParameters(
                        DynamicLink.AndroidParameters.Builder(BuildConfig.APPLICATION_ID)
                            .setMinimumVersion(1)
                            .build()
                    )
                    .setIosParameters(
                        DynamicLink.IosParameters.Builder("com.5k.userapp").setAppStoreId("6443796046")
                            .setMinimumVersion("1.0.0").build()
                    )
                    .buildShortDynamicLink()
                    .addOnSuccessListener { shortDynamicLink ->
                        prefsHelper.intShortLinkPref = shortDynamicLink.shortLink?.toString()

                    }.addOnFailureListener {
                        it.printStackTrace()
                    }


                if (response.data.name.isEmpty()) {
                    val fragment =
                        NameUpdateBottomSheetFragment.newInstance(object :
                            NameUpdateBottomSheetFragment.ItemClickListener {
                            override fun onSubmitClick(name: String) {
                                userName = name
                                val body = LoginRequestBodies.UpdateNameBody(
                                    prefsHelper.intUserIDPref!!,
                                    userName
                                )
                                viewModel.updateUserName(body)
                            }
                        })
                    fragment.isCancelable = false
                    fragment.show(supportFragmentManager, fragment.tag)
                } else if (response.data.name.isNotEmpty()) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                showToast(response.message)
            }
        }

        viewModel.updateUserNameResponse.observe(this) { response ->
            prefsHelper.intUserNamePref = userName
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