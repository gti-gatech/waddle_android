package com.appzoro.milton.ui.view.fragment.sign_up

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.R
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.LoginResponse
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

class SignUpViewModel{}

  /*  (application: Application) : AndroidViewModel(application), Observable,
    Callback<SignUpModel> {

    var mApplication: Application? = null

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
                    Event(mApplication!!.getString(R.string.email_invalid_error_msg))
            }
            else -> {
                //call api for email
                //toastMessage.value = Event("Congratulations..!!${inputEmail.value}")
                // mApplication?.startActivity(Intent(mApplication, TermConditionActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//                if (mApplication != null)
//                    forgotPassword(inputEmail.value.toString().trim(), mApplication!!)

            }
        }
    }

    private fun signUp(mEmailId: String, mApplication: Application) {
        loaderDialog.value = Event(true)

        var temError=""

        val mJson = JsonObject()
        mJson.addProperty("email", mEmailId)

        AppLogger.e("forgot password mJson $mJson ")

        RetrofitService.getInstance(mApplication).getApi()
            .forgotPassword(mJson)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication).mMessage
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
//                        hideLoading()
                        loaderDialog.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))

//                        toastMessage.value = Event((mResponse.message ?: ""))
                        errorDialog.value = Event(DialogMessage(mResponse.type, mResponse.message))

//                        Utils.alertDialog(
//                            mApplication,
//                            (mResponse.type ?: ""),
//                            (mResponse.message ?: "")
//                        )

//                        if (t.statusCode == ResponseCode.success.toInt()) {
//                            getMvpView().isSuccess(t)
//                        } else getMvpView().isErrorWithTitle(
//                            mActivitys!!.getString(R.string.error),
//                            t.message.toString()
//                        )
                    }

                    override fun onError(e: Throwable) {
                        loaderDialog.value = Event(false)
                        e.printStackTrace()


                        errorDialog.value = Event(DialogMessage("Bad Credentials",
                            temError))
                    }
                })
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun onFailure(call: Call<SignUpModel>, t: Throwable) {

    }*/

   /* override fun onResponse(call: Call<SignUpModel>, response: Response<SignUpModel>) {
    }


}
*/


