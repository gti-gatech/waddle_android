package com.appzoro.milton.ui.view.activity.group_details

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.R
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.LoginResponse
import com.appzoro.milton.model.MediaUploadResponse
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
import okhttp3.MultipartBody

class GroupDetailsViewModel(application: Application) : AndroidViewModel(application),Observable {
    private var mApplication: Application? = null
    init {
        mApplication = application
    }

    private val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData

    @Bindable
    val groupName = MutableLiveData<String>()

    @Bindable
    val groupId = MutableLiveData<String>()

    @Bindable
    val imageUrl = MutableLiveData<String>()

    fun callApiForUpdateGroupDetails() {
        AppLogger.e("callApiForAddStudent")
        when {
            groupName.value == null -> callbackData.value =
                Event(mApplication!!.getString(R.string.enter_name))

            groupName.value == "" -> callbackData.value =
                Event(mApplication!!.getString(R.string.enter_name))
            else -> {
                val mJson = JsonObject()
                mJson.addProperty("groupName", groupName.value.toString().trim())
                mJson.addProperty("groupId", groupId.value.toString().trim())
                mJson.addProperty("image", imageUrl.value.toString().trim())
                AppLogger.e("Update Profile mJson $mJson ")
                callApiForUpdateDetails(mJson)
            }
        }

    }

    private fun callApiForUpdateDetails(mJson: JsonObject) {
        callbackData.value = Event(true)
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(mApplication!!)

        RetrofitService.getInstance(mApplication!!).getApi()
            .updateGroupDetails(mJson, (mPreferenceManager.getString(Constant.authToken)?:""))
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonObjectResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: CommonObjectResponse) {
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

     fun callApiForUploadImage(fileBody: MultipartBody.Part) {
        var temError: ErrorFailure? = null
        val mPreferenceManager = PreferenceManager(mApplication!!)
        RetrofitService.getInstance(mApplication!!).getApi()
            .uploadImage(fileBody, (mPreferenceManager.getString(Constant.authToken)?:""))
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<MediaUploadResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: MediaUploadResponse) {
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

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}