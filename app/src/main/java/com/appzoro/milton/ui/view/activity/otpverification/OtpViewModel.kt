package com.appzoro.milton.ui.view.activity.otpverification

import android.app.Application
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.R
import com.appzoro.milton.model.*
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OtpViewModel(application: Application) : AndroidViewModel(application), Observable {
    private var mApplication: Application? = null

    init {
        mApplication = application
    }

    val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData

    fun callApiForSendOtp(email: String) {

        val mJson = JsonObject()
        mJson.addProperty("email", email)

        AppLogger.e("SendOtp mJson $mJson ")

        callbackData.value = Event(true)
        var temError: ErrorFailure? = null

        RetrofitService.getInstance(mApplication!!).getApi()
            .sendOtp(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
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
                        callbackData.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callbackData.value = Event(mResponse)
                    }

                    override fun onError(e: Throwable) {
                        callbackData.value = Event(false)
                        e.printStackTrace()
                        callbackData.value = Event(
                            (temError?.mMessage ?: Constant.emptyString)
                        )
                    }

                })
    }

    fun verifyOtp(mJsonRegistration: JsonObject?, email: String, otp: String) {
        when {
            otp.isEmpty() -> callbackData.value =
                Event(mApplication!!.getString(R.string.otp_empty_validation))
            otp.length < 6 -> callbackData.value =
                Event(mApplication!!.getString(R.string.otp_length_validation))
            else -> {
                val mJson = JsonObject()
                mJson.addProperty("email", email)
                mJson.addProperty("otp", otp)
                AppLogger.e("verifyOtp mJson $mJson ")
                callApiForVerifyOtp(mJsonRegistration, mJson)
            }
        }
    }

    private fun callApiForVerifyOtp(mJsonRegistration: JsonObject?, mJson: JsonObject) {
        callbackData.value = Event(true)
        var temError: ErrorFailure? = null

        RetrofitService.getInstance(mApplication!!).getApi()
            .verifyOtp(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
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
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callApiForUserSignUp(mJsonRegistration ?: JsonObject())
                    }

                    override fun onError(e: Throwable) {
                        callbackData.value = Event(false)
                        e.printStackTrace()
                        callbackData.value = Event(
                            (temError?.mMessage ?: Constant.emptyString)
                        )
                    }

                })
    }

    private fun callApiForUserSignUp(mJson: JsonObject) {
        AppLogger.e("callApiForUserSignUp mJson $mJson ")
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .userRegister(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Observer<LoginResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: LoginResponse) {
                        callbackData.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callbackData.value = Event(mResponse)
                    }

                    override fun onError(e: Throwable) {
                        callbackData.value = Event(false)
                        e.printStackTrace()
                        callbackData.value = Event(
                            (temError?.mMessage ?: Constant.emptyString)
                        )
                    }

                })
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}