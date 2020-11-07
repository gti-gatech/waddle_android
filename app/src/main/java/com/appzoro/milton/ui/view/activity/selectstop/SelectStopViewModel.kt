package com.appzoro.milton.ui.view.activity.selectstop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.SelectedStudentIdModel
import com.appzoro.milton.network.*
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class SelectStopViewModel(application: Application) : AndroidViewModel(application) {
    var mApplication: Application? = null
    var joinGroupResponse: MutableLiveData<CommonObjectResponse>? = null


    init {
        mApplication = application
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

    private val callbackData = MutableLiveData<Event<Any>>()
    val callback: LiveData<Event<Any>>
        get() = callbackData


    fun callApiForGetMapData() {
        if (mApplication != null)
            fetchMapData(mApplication!!)
    }


    fun callApiForJoinGroup(
        groupId: String,
        stopId: String,
        selectedStudentId: ArrayList<SelectedStudentIdModel>
    ) {
        loaderDialog.value = Event(true)
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .joinGroup(
                JsonRequestBody().joinGroupRequest(
                    groupId, stopId, selectedStudentId
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

                    override fun onNext(mListResponse: CommonObjectResponse) {
                        loaderDialog.value = Event(false)
                        joinGroupResponse?.value = mListResponse

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


    private fun fetchMapData(mApplication: Application) {
        loaderDialog.value = Event(true)
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication).getApi()
            .getCommonStop(ApiEndPoint.COMMON_STOP)
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mApplication)
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
                        callbackData.value = Event(mListResponse)

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

    internal fun fetchMapDataBasedRouteId(routeId: String) {
        val url = ApiEndPoint.ROUTE_STOP_BY_ID + routeId
        loaderDialog.value = Event(true)
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mApplication!!).getApi()
            .getCommonStopByRoute(url)
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
                        callbackData.value = Event(mListResponse)

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
}