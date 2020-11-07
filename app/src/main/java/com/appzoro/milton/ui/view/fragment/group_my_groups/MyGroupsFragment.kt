package com.appzoro.milton.ui.view.fragment.group_my_groups

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.group_details.GroupDetailsActivity
import com.appzoro.milton.ui.view.fragment.dashboardschedule.OnThreeDotCLick
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import kotlinx.android.synthetic.main.fragment_group_details_students.view.*

class MyGroupsFragment : BaseFragment() {

    private var mSharedPreferences: PreferenceManager? = null
    private var mAdapter: MyGroupsAdapter? = null
    private var listGroupData: ArrayList<DatumModel>? = null

    private var mView: View? = null
    private var mActivity: Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_group, container, false)
        mView = root
        return root
    }

    override fun setUp(view: View) {
        mActivity = activity
        mSharedPreferences = PreferenceManager(mActivity!!)
        callApiForGetGroupData()
        mAdapter = MyGroupsAdapter(mActivity!!, object : OnThreeDotCLick {
            override fun onItemClick(view: View, pos: Int, type: Int) {
                if (type == 0)
                    showPopUpMenu(view, pos, listGroupData?.get(pos)?.getGroupId()?:0)
                else
                    redirectToGroupDetails(listGroupData?.get(pos)?.getGroupId()?:0)
            }
        })
        view.mRecyclerView?.adapter = mAdapter


    }

    private fun redirectToGroupDetails(groupId: Int) {
        startActivity(
            Intent(mActivity!!, GroupDetailsActivity::class.java)
                .putExtra("groupId", groupId.toString()).putExtra(
                    "comeFrom",
                    "groupNav"
                )
        )
    }

    private fun showPopUpMenu(view: View, pos: Int, groupId: Int?) {
        val popUpMenu = PopupMenu(activity!!, view)
        popUpMenu.menuInflater.inflate(R.menu.group_pop_up_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener {
            if (it.title == getString(R.string.leaveGroup))
                leaveGroupDialog(pos, groupId)
            return@setOnMenuItemClickListener true
        }
        popUpMenu.show()
    }

    private fun leaveGroupDialog(pos: Int, groupId: Int?) {
        val mDialog = Utils.alertDialogWithTwoButton(
            activity!!,
            getString(R.string.are_you_sure_want_leave_this_group)
        )

        mDialog.buttonYes.setOnClickListener {
            mDialog.dismiss()
            callApiForLeaveGroup(pos, groupId)
        }
    }


    private fun callApiForLeaveGroup(pos: Int, groupId: Int?) {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi().leaveGroup(
            ApiEndPoint.LEAVE_GROUPS + groupId, mSharedPreferences?.getString(Constant.authToken)?:""
        ).doOnError {
            temError = UtilThrowable.mCheckThrowable(it, activity!!)
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
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        listGroupData?.removeAt(pos)
                        mAdapter?.notifyItemRemoved(pos)
                        mAdapter?.notifyDataSetChanged()
                        if (listGroupData?.size == 0) {
                            view?.tvNoData?.isVisible = true
                            view?.mRecyclerView?.isVisible = false
                        }
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(activity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })

    }

    private fun callApiForGetGroupData() {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi().getMyGroups(
            ApiEndPoint.GET_GROUPS, mSharedPreferences?.getString(Constant.authToken)?:""
        ).doOnError {
            temError = UtilThrowable.mCheckThrowable(it, activity!!)
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
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))

                        if (mListResponse.getData()?.size ?: 0 > 0) {
                            listGroupData = ArrayList()
                            listGroupData = mListResponse.getData()
                            mAdapter?.setList(listGroupData!!)
                            view?.tvNoData?.isVisible = false
                            view?.mRecyclerView?.isVisible = true
                        } else {
                            view?.tvNoData?.isVisible = true
                            view?.mRecyclerView?.isVisible = false
                            mAdapter?.setList(ArrayList())
                        }
                        mAdapter?.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(activity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) fragmentManager!!.beginTransaction().detach(this).attach(this).commit()

    }

}
