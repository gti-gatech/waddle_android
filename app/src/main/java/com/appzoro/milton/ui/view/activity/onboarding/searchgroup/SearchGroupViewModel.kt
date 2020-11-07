package com.appzoro.milton.ui.view.activity.onboarding.searchgroup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Event
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchGroupViewModel(application: Application) : AndroidViewModel(application) {
    var mApplication: Application? = null
    var groupListData: MutableLiveData<CommonListResponse>? = null

    init {
        mApplication = application
        groupListData= MutableLiveData()
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

    fun callApiForSearchGroup(){
        if(mApplication!=null)
            fetchListData()
    }
    private fun fetchListData() {

        loaderDialog.value = Event(true)

        var temError: ErrorFailure? = null

        RetrofitService.getInstance(mApplication!!).getApi()
            .getGroupList(ApiEndPoint.SEARCH_GROUP)
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
                        groupListData?.value=mListResponse

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