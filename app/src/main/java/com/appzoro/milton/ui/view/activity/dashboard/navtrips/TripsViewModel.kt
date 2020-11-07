package com.appzoro.milton.ui.view.activity.dashboard.navtrips

import android.app.Application
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.LoginResponse
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.appzoro.milton.utility.PreferencesData
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TripsViewModel(application: Application) : AndroidViewModel(application),Observable {
    private var mApplication: Application? = null

    init {
        mApplication = application
    }


    private val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData


    private fun callApiForUpdateProfile(mJson: JsonObject) {
        callbackData.value = Event(true)
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(mApplication!!)

        RetrofitService.getInstance(mApplication!!).getApi()
            .updateParentProfile(mJson, (mPreferenceManager.getString(Constant.authToken)?:""))
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<LoginResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: LoginResponse) {
                        callbackData.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        PreferencesData.setLoginPreference(mApplication!!, mResponse.data)
                        callbackData.value = Event(mResponse)
                        callbackData.value = Event(mResponse.message)
                    }

                    override fun onError(e: Throwable) {
                        callbackData.value = Event(false)
                        e.printStackTrace()
                        callbackData.value = Event(
                            DialogMessage(
                                (temError?.mTitle ?: Constant.emptyString),
                                (temError?.mMessage ?: Constant.emptyString)
                            )
                        )
                    }

                })
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}