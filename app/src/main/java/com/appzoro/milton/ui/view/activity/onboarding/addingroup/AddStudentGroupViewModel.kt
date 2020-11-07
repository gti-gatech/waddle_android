package com.appzoro.milton.ui.view.activity.onboarding.addingroup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.SelectedStudentIdModel
import com.appzoro.milton.network.*
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddStudentGroupViewModel(application: Application) : AndroidViewModel(application) {
    var mApplication: Application? = null
    var studentListData: MutableLiveData<CommonListResponse>? = null
    var joinGroupResponse: MutableLiveData<CommonObjectResponse>? = null


    init {
        mApplication = application
        studentListData = MutableLiveData()
        joinGroupResponse = MutableLiveData()
    }

    private val mPreferenceManager = PreferenceManager(mApplication!!)
    private val toastMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = toastMessage

    private val loaderDialog = MutableLiveData<Event<Boolean>>()
    val loader: LiveData<Event<Boolean>>
        get() = loaderDialog

    private val errorDialog = MutableLiveData<Event<DialogMessage>>()
    val error: LiveData<Event<DialogMessage>>
        get() = errorDialog

    fun callApiForGetStudent() {
        if (mApplication != null)
            fetchStudentListData()
    }

    fun callApiForJoinGroup(
        groupId: String,
        stopId: String,
        selectedStudentId: ArrayList<SelectedStudentIdModel>
    ) {
        if (mApplication != null)
            joinGroupApiCall(groupId, stopId, selectedStudentId)
    }

    private fun fetchStudentListData() {
        loaderDialog.value = Event(true)
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .getStudentList(
                ApiEndPoint.STUDENT_LIST,
                mPreferenceManager.getString(Constant.authToken)!!
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
                        studentListData?.value = mListResponse
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

    private fun joinGroupApiCall(
        groupId: String,
        stopId: String,
        selectedStudentId: ArrayList<SelectedStudentIdModel>
    ) {
        loaderDialog.value = Event(true)
        var tempError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .joinGroup(
                JsonRequestBody().joinGroupRequest(
                    groupId, stopId, selectedStudentId
                ), mPreferenceManager.getString(Constant.authToken).toString()
            )
            .doOnError {
                tempError = UtilThrowable.mCheckThrowable(it, mApplication!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<CommonObjectResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: CommonObjectResponse) {
                        loaderDialog.value = Event(false)
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        joinGroupResponse?.value = mListResponse

                    }

                    override fun onError(e: Throwable) {
                        loaderDialog.value = Event(false)
                        e.printStackTrace()
                        errorDialog.value = Event(
                            DialogMessage(
                                (tempError?.mTitle ?: Constant.emptyString),
                                (tempError?.mMessage ?: Constant.emptyString)
                            )
                        )
                    }

                })

    }

}