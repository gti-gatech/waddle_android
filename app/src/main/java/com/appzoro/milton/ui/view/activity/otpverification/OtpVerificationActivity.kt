package com.appzoro.milton.ui.view.activity.otpverification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivityOtpVerificationBinding
import com.appzoro.milton.model.DefaultResponse
import com.appzoro.milton.model.LoginResponse
import com.appzoro.milton.ui.view.activity.term.TermConditionActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.PreferencesData
import com.appzoro.milton.utility.Utils
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_otp_verification.*

class OtpVerificationActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityOtpVerificationBinding
    private lateinit var mViewModel: OtpViewModel
    private var mActivity: Activity? = null
    private var mPreferences: PreferenceManager? = null

    private var email: String = ""
    private var mJson: JsonObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification)
        mViewModel = ViewModelProviders.of(this).get(OtpViewModel::class.java)
        dataBinding.otpViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        initUi()
    }

    private fun initUi() {
        mActivity = this@OtpVerificationActivity
        mPreferences = PreferenceManager(mActivity!!)

        Utils.setDefaultTitleCenter(supportActionBar, "", this, true)

        btnVerify.setOnClickListener {
            AppLogger.e("otp >> ${mOtpPassword.otp}")
            mViewModel.verifyOtp(mJson, email, mOtpPassword.otp)
        }

        imageCancel.setOnClickListener {
            onBackPressed()
        }

        textViewResendOtp.setOnClickListener {
            mViewModel.callApiForSendOtp(email)
        }

        getRegistrationData()
        manageCallbacks()

    }

    private fun getRegistrationData() {

        if (intent.hasExtra("fullName")) {
            email = intent.getStringExtra("email") ?: ""
            val fullName = intent.getStringExtra("fullName") ?: ""
            val password = intent.getStringExtra("password") ?: ""
            val contact = intent.getStringExtra("contact") ?: ""
            val stopId = intent.getStringExtra("stopId") ?: ""
            val image = intent.getStringExtra("image") ?: ""
            val address = intent.getStringExtra("address") ?: ""
            val deviceToken = intent.getStringExtra("deviceToken") ?: ""

            mJson = JsonObject()
            mJson?.addProperty("fullName", fullName)
            mJson?.addProperty("email", email)
            mJson?.addProperty("password", password)
            mJson?.addProperty("contact", contact)
            mJson?.addProperty("stopId", stopId)
            mJson?.addProperty("image", image)
            mJson?.addProperty("address", address)
            mJson?.addProperty("deviceToken", deviceToken)

            AppLogger.e("mJson $mJson")

        }

    }

    private fun manageCallbacks() {

        mViewModel.callback.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it is String) Utils.alertDialog(this, it)
                else if (it is Boolean) {
                    if (it) showLoading() else hideLoading()
                } else if (it is DefaultResponse) Utils.alertDialog(
                    this,
                    it.message
                ) else if (it is LoginResponse) {
                    mPreferences?.setString(Constant.isLogin, "1")
                    PreferencesData.setLoginPreference(mActivity!!, it.data)

                    val intent = Intent(mActivity!!, TermConditionActivity::class.java)
                        .putExtra("comeFrom", "terms")
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    mActivity?.finish()

                } else {
                    AppLogger.e(" else ")
                }
            }
        })

    }
}