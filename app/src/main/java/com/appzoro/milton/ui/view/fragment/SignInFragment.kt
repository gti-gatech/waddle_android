package com.appzoro.milton.ui.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.LoginResponse
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService.Companion.getInstance
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.forgotpassword.ForgotActivity
import com.appzoro.milton.ui.view.activity.onboarding.OnBoardingActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.PreferencesData
import com.appzoro.milton.utility.Utils
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sign_in.*
import retrofit2.Response

class SignInFragment : BaseFragment(), View.OnClickListener {
    lateinit var mSharedPreferences: PreferenceManager


    object NewInstance {
        fun instance(): SignInFragment {
            val fragment = SignInFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var mView: View? = null
    private var mActivity: Activity? = null
    private var mToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_sign_in, container, false)
        mView = root
        mSharedPreferences = PreferenceManager(activity!!)
        return root
    }

    override fun setUp(view: View) {
        mActivity = activity
        getToken()
        tvForget.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(mActivity!!) { instanceIdResult ->
                mToken = instanceIdResult.token
                AppLogger.e("fcm token: $mToken")
            }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvForget -> {
                startActivity(Intent(mActivity, ForgotActivity::class.java))
            }
            R.id.btnSignIn -> {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (mToken.isEmpty()) getToken()

                if (checkValidation(email, password)) {
                    showLoading()
                    userLogin(email, password)
                }

            }
        }
    }

    private fun checkValidation(email: String, password: String): Boolean {

        var isValid = true
        if (email.isEmpty()) {
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
                mActivity?.getString(R.string.enter_password) ?: ""
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

    private fun userLogin(
        mEmailId: String,
        mPassword: String
    ) {

        val mJson = JsonObject()
        mJson.addProperty("email", mEmailId)
        mJson.addProperty("password", mPassword)
        mJson.addProperty("deviceToken", mToken)
        AppLogger.e("login mJson $mJson ")
        //new code kar ke check kar lo
        //ok

        var temError: ErrorFailure? = null
        getInstance(mActivity!!).getApi()
            .userLogin(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Observer<Response<LoginResponse>> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: Response<LoginResponse>) {
                        hideLoading()
                        if (mResponse.code() == 200) {
                            AppLogger.e(Constant.TAG + Gson().toJson(mResponse.body()))
                            mSharedPreferences.setString(Constant.isLogin, "1")
                            PreferencesData.setLoginPreference(mActivity!!, mResponse.body()?.data)
                            if (mResponse.body()?.data?.parentData?.isFirstTime.equals("1")) {
                                startActivity(
                                    Intent(mActivity, DashBoardActivity::class.java).putExtra(
                                        "comeFrom",
                                        ""
                                    )
                                )
                            } else startActivity(Intent(mActivity, OnBoardingActivity::class.java))
                            mActivity?.finish()
                        } else {
                            Utils.alertDialog(
                                mActivity!!,
                                "The email or password doesn't match our records. Please try again!"
                            )
                            Log.d(Constant.TAG, mResponse.code().toString())
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) fragmentManager!!.beginTransaction().detach(this).attach(this).commit()

    }

}
