package com.appzoro.milton.ui.view.activity.forgotpassword

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.R
import com.appzoro.milton.model.DefaultResponse
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotViewModel(application: Application) : AndroidViewModel(application), Observable,
    Callback<ForgotModel> {
    private var mApplication: Application? = null

    init {
        mApplication = application
    }

    private val toastMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = toastMessage

    private val loaderDialog = MutableLiveData<Event<Boolean>>()
    val loader: LiveData<Event<Boolean>>
        get() = loaderDialog

    private val errorDialog = MutableLiveData<Event<DialogMessage>>()
    val error: LiveData<Event<DialogMessage>>
        get() = errorDialog

    @Bindable
    val inputEmail = MutableLiveData<String>()

    fun callApiForSentEmail() {
        when {
            inputEmail.value == null -> {
                toastMessage.value = Event(mApplication!!.getString(R.string.email_empty_error_msg))
            }
            !Utils.isEmailValidation(inputEmail.value!!) -> {
                toastMessage.value =
                    Event(mApplication!!.getString(R.string.invalid_email))
            }
            else -> {
                //call api for email
                //toastMessage.value = Event("Congratulations..!!${inputEmail.value}")
                // mApplication?.startActivity(Intent(mApplication, TermConditionActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                if (mApplication != null)
                    forgotPassword(inputEmail.value.toString().trim(), mApplication!!)

            }
        }
    }

    private fun forgotPassword(mEmailId: String, mApplication: Application) {
        loaderDialog.value = Event(true)

        var temError: ErrorFailure? = null

        val mJson = JsonObject()
        mJson.addProperty("email", mEmailId)

        AppLogger.e("forgot password mJson $mJson ")

        RetrofitService.getInstance(mApplication).getApi()
            .forgotPassword(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<Response<DefaultResponse>> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: Response<DefaultResponse>) {
                        if (mResponse.code() == 200) {
                            loaderDialog.value = Event(false)
                            AppLogger.e(Constant.TAG + Gson().toJson(mResponse.body()))
                            errorDialog.value =
                                Event(DialogMessage(mResponse.body()!!.type, mResponse.body()!!.message))
                        }
                        else{
                            errorDialog.value = Event(
                                DialogMessage(
                                    (mResponse.body()?.type ?: ""),
                                    (mResponse.message() ?: "")
                                )
                            )
                        }

                    }

                    override fun onError(e: Throwable) {
                        loaderDialog.value = Event(false)
                        e.printStackTrace()
                        errorDialog.value = Event(
                            DialogMessage(
                                (temError?.mTitle ?: ""),
                                (temError?.mMessage ?: "")
                            )
                        )
                    }

                })
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun onFailure(call: Call<ForgotModel>, t: Throwable) {

    }

    override fun onResponse(call: Call<ForgotModel>, response: Response<ForgotModel>) {
    }


}



