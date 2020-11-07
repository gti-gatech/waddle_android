package com.appzoro.milton.ui.view.activity.notification

import android.app.Application
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.DefaultResponse
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.NotificationsListResponse
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NotificationsViewModel(application: Application) : AndroidViewModel(application), Observable {
    private var mApplication: Application? = null
    init {
        mApplication = application
    }
    private val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData
    fun callApiForGetNotifications() {
        callbackData.value = Event(true)
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(mApplication!!)
        val authToken = (mPreferenceManager.getString(Constant.authToken) ?: "")
        AppLogger.e("authToken>> $authToken")

        RetrofitService.getInstance(mApplication!!).getApi()
            .getNotifications(authToken)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<NotificationsListResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: NotificationsListResponse) {
                        callbackData.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callbackData.value = Event(mResponse)
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

    fun callApiForNotificationReadMark(id: String) {
        callbackData.value = Event(true)
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(mApplication!!)

        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        AppLogger.e("jsonObject $jsonObject")
        RetrofitService.getInstance(mApplication!!).getApi()
            .notificationReadMark(
                jsonObject,
                (mPreferenceManager.getString(Constant.authToken) ?: "")
            )
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<DefaultResponse> {
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
                            DialogMessage(
                                (temError?.mTitle ?: Constant.emptyString),
                                (temError?.mMessage ?: Constant.emptyString)
                            )
                        )
                    }

                })
    }

    fun callApiForTripsStart(id: String) {
        callbackData.value = Event(true)
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(mApplication!!)

        val url = ApiEndPoint.BASE_URL + ApiEndPoint.TRIPS_START + id
        val authToken = (mPreferenceManager.getString(Constant.authToken) ?: "")
        AppLogger.e("url >>> $url")

        RetrofitService.getInstance(mApplication!!).getApi()
            .tripsStart(url, authToken)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<DefaultResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: DefaultResponse) {
                        //callbackData.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        //callbackData.value = Event(mResponse)
                        callApiForNotificationReadMark(id)
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