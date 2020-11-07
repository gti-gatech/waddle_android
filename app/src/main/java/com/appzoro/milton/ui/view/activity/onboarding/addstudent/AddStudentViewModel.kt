package com.appzoro.milton.ui.view.activity.onboarding.addstudent

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.R
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.network.*
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddStudentViewModel(application: Application) : AndroidViewModel(application), Observable {
    var mApplication: Application? = null
    var addStudentListResponse: MutableLiveData<CommonListResponse>? = null
    var studentItem: MutableLiveData<DatumModel>? = null
    var comeFrom: String = ""

    init {
        mApplication = application
        addStudentListResponse = MutableLiveData()
        studentItem = MutableLiveData()
    }

    private val mPreferenceManager = PreferenceManager(mApplication!!)
    private val toastMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = toastMessage

    private val loaderDialog = MutableLiveData<Event<Boolean>>()
    val loader: LiveData<Event<Boolean>>
        get() = loaderDialog

    val errorDialog = MutableLiveData<Event<DialogMessage>>()
    val error: LiveData<Event<DialogMessage>>
        get() = errorDialog

    private val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData

    @Bindable
    val inputStudentName = MutableLiveData<String>()

    @Bindable
    val inputParentName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val inputSchoolName = MutableLiveData<String>()

    @Bindable
    val inputStudentGrade = MutableLiveData<String>()

    fun addStudent() {
        when {
            inputStudentName.value == null -> {
                toastMessage.value =
                    Event(mApplication!!.getString(R.string.student_name_validation_msg))
            }
            inputEmail.value == null -> {
                toastMessage.value =
                    Event(mApplication!!.getString(R.string.email_validation_msg))
            }

            !Utils.isEmailValidation(inputEmail.value!!) -> {
                toastMessage.value =
                    Event(mApplication!!.getString(R.string.invalid_email))
            }

            inputSchoolName.value == null -> {
                toastMessage.value =
                    Event(mApplication!!.getString(R.string.school_name_validation_msg))
            }
            inputStudentGrade.value == null -> {
                toastMessage.value =
                    Event(mApplication!!.getString(R.string.student_grade_validation_msg))
            }
            else -> {
                AppLogger.e("comeFrom $comeFrom")
                if (Utils.isEmailValidation(inputEmail.value.toString())) {
                    if (comeFrom == "edit") {
                        callApiForEditStudent()
                    } else {
                        callApiForAddStudent()
                    }
                } else {
                    toastMessage.value =
                        Event(mApplication!!.getString(R.string.invalid_email))
                }

            }
        }

    }

    private fun callApiForAddStudent() {
        loaderDialog.value = Event(true)
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .addStudent(
                JsonRequestBody().addStudentJsonRequest(
                    inputStudentName.value.toString(),
                    inputEmail.value.toString(),
                    inputSchoolName.value.toString(),
                    inputStudentGrade.value.toString()
                ), mPreferenceManager.getString(Constant.authToken).toString()
            )
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonListResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: CommonListResponse) {
                        loaderDialog.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        addStudentListResponse?.value = mListResponse
                    }

                    override fun onError(e: Throwable) {
                        loaderDialog.value = Event(false)
                        e.printStackTrace()
                        errorDialog.value = Event(
                            DialogMessage(
                                (temError?.mTitle ?: Constant.emptyString),
                                (temError?.mMessage ?: Constant.emptyString)
                            )
                        )
                    }

                })
    }

    private fun callApiForEditStudent() {
        val url: String = ApiEndPoint.EDIT_STUDENT + (studentItem?.value?.getId() ?: "")
        AppLogger.e("EDIT_STUDENT url $url")
        loaderDialog.value = Event(true)
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .editStudentStudent(
                url,
                JsonRequestBody().addStudentJsonRequest(
                    inputStudentName.value.toString(),
                    inputEmail.value.toString(),
                    inputSchoolName.value.toString(),
                    inputStudentGrade.value.toString()
                ), mPreferenceManager.getString(Constant.authToken).toString()
            )
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
                        loaderDialog.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callbackData.value = Event(mResponse)
                    }

                    override fun onError(e: Throwable) {
                        loaderDialog.value = Event(false)
                        e.printStackTrace()
                        errorDialog.value = Event(
                            DialogMessage(
                                (temError?.mTitle ?: Constant.emptyString),
                                (temError?.mMessage ?: Constant.emptyString)
                            )
                        )
                    }

                })
    }

    fun callApiForDeleteStudent() {

        val url: String = ApiEndPoint.DELETE_STUDENT + (studentItem?.value?.getId() ?: "")
        AppLogger.e("DELETE_STUDENT url $url")

        loaderDialog.value = Event(true)
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .deleteStudent(
                url,
                mPreferenceManager.getString(Constant.authToken).toString()
            )
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
                        loaderDialog.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callbackData.value = Event(mResponse)
                    }

                    override fun onError(e: Throwable) {
                        loaderDialog.value = Event(false)
                        e.printStackTrace()
                        errorDialog.value = Event(
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