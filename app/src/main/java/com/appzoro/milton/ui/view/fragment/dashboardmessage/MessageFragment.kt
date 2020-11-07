package com.appzoro.milton.ui.view.fragment.dashboardmessage

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.MessageListResponse
import com.appzoro.milton.model.MessageModel
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.message_fragment_header.view.*
import kotlinx.android.synthetic.main.fragment_message.view.*
import kotlinx.android.synthetic.main.fragment_message.view.layoutHeader1
import kotlinx.android.synthetic.main.fragment_message.view.layoutHeader2
import kotlinx.android.synthetic.main.message_fragment_search_header.view.*
import java.util.*
import kotlin.collections.ArrayList

class MessageFragment : BaseFragment() {

    private var mAdapter: MessagesAdapter? = null
    private var mSharedPreferences: PreferenceManager? = null
    private var mActivity: Activity? = null
    private var animationBounce: Animation? = null
    private var mGroupDataList: ArrayList<MessageModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        animationBounce = AnimationUtils.loadAnimation(activity, R.anim.slide_down_fast)
        view.imageNotification.setImageResource(R.drawable.ic_search)
        view.imageNotification.setOnClickListener {
            view.layoutHeader1.clearAnimation()
            view.layoutHeader1.visibility = View.GONE
            view.layoutHeader2.visibility = View.VISIBLE
            view.layoutHeader2.startAnimation(animationBounce)
        }

        view.ivCancelSearch.setOnClickListener {
            AppLogger.e("Click on ivCancelSearch")
            view.layoutHeader2.clearAnimation()
            view.layoutHeader2.visibility = View.GONE
            view.layoutHeader1.visibility = View.VISIBLE
            view.layoutHeader1.startAnimation(animationBounce)
        }

        view.ivClearALLText.setOnClickListener {
            view.etSearch.setText("")
            setMessageDataList(mGroupDataList)
        }

        view.imageViewHamburger.setOnClickListener {
            Utils.drawerOpenClose(drawerLayout = activity!!.drawer_layout)
        }
        return view
    }

    override fun setUp(view: View) {
        mActivity = activity
        mSharedPreferences = PreferenceManager(mActivity!!)
        view.mRecyclerView?.setHasFixedSize(false)
        mAdapter = MessagesAdapter(mActivity!!)
        view.mRecyclerView?.adapter = mAdapter

        view.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {

                AppLogger.e("editable text ${editable.toString()}")
                if (editable != null) {
                    if (editable.toString().isNotEmpty()) {
                        val groupName = editable.toString().toLowerCase(Locale.ROOT)
                        searchGroupNameFromGroupList(groupName)
                    }else setMessageDataList(mGroupDataList)
                }
            }
        })
    }

    private fun searchGroupNameFromGroupList(groupName: String) {

        val mGroupList: ArrayList<MessageModel> = ArrayList()
        if (mGroupDataList?.size ?: 0 > 0) {

            for (mGroupData in mGroupDataList!!) {
                if ((mGroupData.groupName).toLowerCase(Locale.ROOT).contains(groupName)) {
                    mGroupList.add(mGroupData)
                }
            }
        }

        mAdapter?.setList(mGroupList)
        mAdapter?.notifyDataSetChanged()

        if (mGroupList.size > 0) {
            view?.tvNoData?.visibility = View.GONE
        } else {
            view?.tvNoData?.visibility = View.VISIBLE
        }


    }

    private fun callApiForGetMessages() {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .getMessagesList((mSharedPreferences?.getString(Constant.authToken) ?: ""))
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
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
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        setMessageDataList(mResponse.data)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        e.printStackTrace()
                        setMessageDataList(null)
                        Utils.alertDialog(mActivity!!, (temError?.mMessage ?: ""))
                    }

                })
    }

    private fun setMessageDataList(dataList: ArrayList<MessageModel>?) {

        mGroupDataList = dataList ?: ArrayList()
        mAdapter?.setList(mGroupDataList)
        mAdapter?.notifyDataSetChanged()

        if (mGroupDataList?.size ?: 0 > 0) {
            view?.tvNoData?.visibility = View.GONE
        } else {
            view?.tvNoData?.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
          callApiForGetMessages()
    }

}