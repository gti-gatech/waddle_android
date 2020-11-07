package com.appzoro.milton.ui.view.fragment.sign_up

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.DefaultResponse
import com.appzoro.milton.model.LoginResponse
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.otpverification.OtpVerificationActivity
import com.appzoro.milton.ui.view.activity.selectstop.SelectStopActivity
import com.appzoro.milton.ui.view.activity.term.TermConditionActivity
import com.appzoro.milton.utility.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sign_up.*
import retrofit2.Response

class SignUpFragment : BaseFragment(), View.OnClickListener {

    private var mView: View? = null
    private var mActivity: Activity? = null
    private var mPreference: PreferenceManager? = null
    private var mToken: String = ""
    private var location: String = ""
    private var stopId: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_sign_up, container, false)
        mView = root
        return root
    }

    override fun setUp(view: View) {
        mActivity = activity
        mPreference = PreferenceManager(mActivity!!)
        getToken()

        //todo need to work
        etPhoneNumber.addTextChangedListener(PhoneTextFormatter(etPhoneNumber, "(###) ###-####"))
        AppLogger.e("SignUP setUp")
        btnSignUp.setOnClickListener(this)
        tvDefaultStop.setOnClickListener(this)
//        tvAgreeTerms.setOnClickListener(this)

        tvAgreeTerms.makeLinks(
            Pair("Terms & Conditions", View.OnClickListener {
                AppLogger.e(" Terms &amp; Conditions ")

                startActivity(
                    Intent(activity, TermConditionActivity::class.java)
                        .putExtra("comeFrom", "terms")
                )

            }),
            Pair("Privacy Policy", View.OnClickListener {
                AppLogger.e(" Privacy Policy ")
                startActivity(
                    Intent(activity, TermConditionActivity::class.java)
                        .putExtra("comeFrom", "privacy")
                )

            })
        )

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.tvDefaultStop -> {
                val intent = Intent(activity, SelectStopActivity::class.java)
                intent.putExtra("comeFrom", "signUp")
                startActivityForResult(intent, 222)
            }

            R.id.tvAgreeTerms -> {
                startActivity(
                    Intent(activity, TermConditionActivity::class.java)
                        .putExtra("comeFrom", "terms")
                )
            }

            R.id.btnSignUp -> {
                clickOnSignUP()
            }
        }
    }

    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {

        val text = getString(R.string.by_creating_terms_conditions)
        val spannableString = SpannableString(text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {

                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
                    // textPaint.color = ds.linkColor
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    // textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = text.indexOf(link.first)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun clickOnSignUP() {

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val conPassword = etConPassword.text.toString().trim()
        var phoneNumber = etPhoneNumber.text.toString().trim()
        val address = etAddress.text.toString().trim()
//                val defaultStop = tvDefaultStop.text.toString().trim()

        var profileImage = mPreference?.getString(Constant.profileImage) ?: ""
        val profileImageUrl = mPreference?.getString(Constant.profileImage_url) ?: ""

        if (checkValidation(
                name, email, password, conPassword, phoneNumber, address,
                stopId
            )
        ) {

            if (profileImageUrl.isNotEmpty()) {
                profileImage = profileImageUrl
            }

            phoneNumber = phoneNumber.replace("[^a-zA-Z0-9 ]".toRegex(), "")
            phoneNumber = phoneNumber.replace(" ", "")

            val mJson = JsonObject()
            mJson.addProperty("fullName", name)
            mJson.addProperty("email", email)
            mJson.addProperty("password", password)
            mJson.addProperty("contact", phoneNumber)
            mJson.addProperty("stopId", stopId)
            mJson.addProperty("image", profileImage)
            mJson.addProperty("address", address)
            mJson.addProperty("deviceToken", mToken)

            AppLogger.e("SignUp mJson $mJson ")


            val intent = Intent(mActivity!!, OtpVerificationActivity::class.java)
            intent.putExtra("fullName", name)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            intent.putExtra("contact", phoneNumber)
            intent.putExtra("stopId", stopId)
            intent.putExtra("image", profileImage)
            intent.putExtra("address", address)
            intent.putExtra("deviceToken", mToken)
//            startActivity(intent)

            showLoading()
            callApiForSendOtp(email, intent)

        }

    }

    private fun checkValidation(
        name: String, email: String, password: String, conPassword: String,
        phoneNumber: String, address: String, defaultStop: String
    ): Boolean {

        var isValid = true
        if (name.isEmpty()) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.enter_name) ?: ""
            )
        } else if (email.isEmpty()) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.enter_email) ?: ""
            )
        } else if (!Utils.isEmailValidation(email)) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.invalid_email) ?: ""
            )
        } else if (password.isEmpty()) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.enter_a_password) ?: ""
            )
        } else if (conPassword.isEmpty()) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.please_confirm_password) ?: ""
            )
        } else if (password != conPassword) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.confirm_password_validation) ?: ""
            )

        } else if (phoneNumber.isEmpty()) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.phone_empty_validation) ?: ""
            )
        } else if (phoneNumber.length < 14) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.phone_length_validation) ?: ""
            )

        } else if (address.isEmpty()) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.address_empty_validation) ?: ""
            )

        } else if (defaultStop.isEmpty()) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.default_default_validation) ?: ""
            )

        } else if (!Utils.isOnline(mActivity!!)) {
            isValid = false
            Utils.alertDialog(
                mActivity!!,
                mActivity?.getString(R.string.no_internet_connection) ?: ""
            )
        }

        return isValid
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(mActivity!!) { instanceIdResult ->
                mToken = instanceIdResult.token
                AppLogger.e("fcm token: $mToken")
            }

    }

    private fun callApiForSendOtp(email: String, intent: Intent) {

        val mJson = JsonObject()
        mJson.addProperty("email", email)
        AppLogger.e("SendOtp mJson $mJson ")

        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .sendOtp(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Observer<DefaultResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: DefaultResponse) {
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))

                        if (mResponse.type == "SUCCESS") {
                            startActivity(intent)
                        } else {
                            Utils.alertDialog(mActivity!!, mResponse.message)
                        }

                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        e.printStackTrace()

                        Utils.alertDialog(
                            mActivity!!, (temError?.mMessage ?: "")
                        )
                    }

                })
    }

    private fun userSignUp(mJson: JsonObject) {

        AppLogger.e("SignUp mJson $mJson ")

        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .userRegister(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Observer<LoginResponse>{
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: LoginResponse) {
                        hideLoading()
//                        if (mResponse.code() == 200) {
//                            AppLogger.e(Constant.TAG + Gson().toJson(mResponse.body()))
                            //Utils.alertDialog(mActivity!!, mResponse.message)
                            mPreference?.setString(Constant.isLogin, "1")
                            PreferencesData.setLoginPreference(mActivity!!, mResponse.data)
                            startActivity(
                                Intent(mActivity!!, TermConditionActivity::class.java)
                                    .putExtra("comeFrom", "terms")
                            )
                            mActivity?.finish()
                        /*}
                        else{
                            Utils.alertDialog(
                                mActivity!!, (mResponse.message() ?: "")
                            )
                        }*/
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        e.printStackTrace()

                        Utils.alertDialog(
                            mActivity!!, (temError?.mMessage ?: "")
                        )
                    }

                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogger.e("Fragment requestCode  $requestCode")
        if (requestCode == 222) {
            val isLocation = data?.hasExtra("location") ?: false
            if (isLocation) {
                location = data?.getStringExtra("location") ?: ""
                stopId = data?.getStringExtra("stopId") ?: ""

                tvDefaultStop.text = location
            }

            AppLogger.e("location  $location")
            AppLogger.e("stopId  $stopId")

        }
    }

    override fun onDestroy() {
        mPreference?.setString(Constant.profileImage_url, "")
        super.onDestroy()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) fragmentManager!!.beginTransaction().detach(this).attach(this).commit()

    }

}
