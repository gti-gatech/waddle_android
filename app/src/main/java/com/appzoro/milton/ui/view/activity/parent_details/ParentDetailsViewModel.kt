package com.appzoro.milton.ui.view.activity.parent_details

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

class ParentDetailsViewModel(application: Application) : AndroidViewModel(application),Observable {
    private var mApplication: Application? = null

    init {
        mApplication = application
    }


    private val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData

    @Bindable
    val fullName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val phone = MutableLiveData<String>()

    @Bindable
    val address = MutableLiveData<String>()

    @Bindable
    val defaultStopId = MutableLiveData<String>()

    @Bindable
    val defaultStopName = MutableLiveData<String>()

    @Bindable
    val imageUrl = MutableLiveData<String>()

    fun callApiForUpdateProfile() {

        AppLogger.e("callApiForAddStudent")

        var contact = phone.value.toString().trim()
        contact = contact.replace("[^a-zA-Z0-9 ]".toRegex(), "")
        contact = contact.replace(" ","")

        when {
            fullName.value == null -> callbackData.value =
                Event(mApplication!!.getString(R.string.enter_name))

            fullName.value == "" -> callbackData.value =
                Event(mApplication!!.getString(R.string.enter_name))

            phone.value == null -> callbackData.value =
                Event(mApplication!!.getString(R.string.phone_empty_validation))

            phone.value == "" -> callbackData.value =
                Event(mApplication!!.getString(R.string.phone_empty_validation))

            contact.length < 10 -> callbackData.value =
                Event(mApplication!!.getString(R.string.phone_length_validation))

            address.value == null -> callbackData.value =
                Event(mApplication!!.getString(R.string.address_empty_validation))

            address.value == "" -> callbackData.value =
                Event(mApplication!!.getString(R.string.address_empty_validation))

            defaultStopId.value == "" -> callbackData.value =
                Event(mApplication!!.getString(R.string.default_default_validation))

            else -> {
                val mJson = JsonObject()
                mJson.addProperty("fullName", fullName.value.toString().trim())
                mJson.addProperty("contact", contact)
                mJson.addProperty("stopId", defaultStopId.value.toString().trim())
                mJson.addProperty("image", imageUrl.value.toString().trim())
                mJson.addProperty("address", address.value.toString().trim())
                AppLogger.e("Update Profile mJson $mJson ")
                callApiForUpdateProfile(mJson)
            }
        }

    }

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