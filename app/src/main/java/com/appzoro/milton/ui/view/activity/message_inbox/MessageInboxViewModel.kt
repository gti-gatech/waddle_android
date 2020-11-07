package com.appzoro.milton.ui.view.activity.message_inbox

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.R
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.LoginResponse
import com.appzoro.milton.model.MediaUploadResponse
import com.appzoro.milton.model.MessageListResponse
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class MessageInboxViewModel(application: Application) : AndroidViewModel(application),Observable {
    private var mApplication: Application? = null

    init {
        mApplication = application
    }

     val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData

     fun callApiForGroupMessages(groupId: String) {
         val url = ApiEndPoint.GROUP_IN_MESSAGES_LIST + groupId
        callbackData.value = Event(true)
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(mApplication!!)

        RetrofitService.getInstance(mApplication!!).getApi()
            .getGroupsInMessages(url, (mPreferenceManager.getString(Constant.authToken)?:""))
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<MessageListResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: MessageListResponse) {
                        callbackData.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callbackData.value = Event(mResponse)
                    }

                    override fun onError(e: Throwable) {
                        callbackData.value = Event(false)
                        e.printStackTrace()
                        callbackData.value = Event(
                                (temError?.mMessage ?: Constant.emptyString))
                    }

                })
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}