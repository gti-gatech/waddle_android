package com.appzoro.milton.ui.view.fragment.group_details_trips

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.CommonObjectResponse
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.model.GroupDetailsResponse
import com.appzoro.milton.network.ApiEndPoint
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.group_details.GroupDetailsActivity
import com.appzoro.milton.ui.view.activity.trackmap.TrackMapActivity
import com.appzoro.milton.utility.AlertDialogView
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import kotlinx.android.synthetic.main.fragment_group_details_trips.view.*

class GroupDetailsTripsFragment : BaseFragment(), OnClickItemInterface {

    object NewInstance {
        fun instance(): GroupDetailsTripsFragment {
            val fragment =
                GroupDetailsTripsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var mView: View? = null
    private var mActivity: Activity? = null
    private var mAdapter: TripsGroupDetailsAdapter? = null
    private var mPreferences: PreferenceManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_group_details_trips, container, false)
        mView = root
        return root
    }

    override fun setUp(view: View) {
        mActivity = activity
        mPreferences = PreferenceManager(mActivity!!)
        view.mRecyclerView?.setHasFixedSize(false)
        mAdapter = TripsGroupDetailsAdapter(mActivity!!, mPreferences!!, this)
        view.mRecyclerView?.adapter = mAdapter

    }

    fun getTripsList(trips: ArrayList<DatumModel>?) {
        AppLogger.e("groupTrips ${Gson().toJson(trips)}")
        mAdapter?.setList(trips)
        mAdapter?.notifyDataSetChanged()

        if (trips?.size ?: 0 > 0) view?.tvNoData?.visibility =
            View.GONE else view?.tvNoData?.visibility = View.VISIBLE

    }

    override fun onItemClickItem(view: View, pos: Int, item: DatumModel, type: Int) {

        AppLogger.e("onItemClickItem >> ${Gson().toJson(item)}")
        when (type) {
            0 -> showPopupMenu(view, pos, item)
            1 -> callApiForStartTrip(pos, item)
            else -> showConfirmationDialog(pos,item)

        }
    }

    private fun showConfirmationDialog(pos: Int, item: DatumModel) {
        val logoutDialog = AlertDialogView.showDialogWithTwoButton(
            mActivity!!,
            R.drawable.ic_signin_logo,
            getString(R.string.trip_end_msg),
            getString(R.string.yes),
            getString(R.string.no)
        )
        logoutDialog.buttonNo.setOnClickListener {
            logoutDialog.dismiss()

        }
        logoutDialog.buttonYes.setOnClickListener {
            logoutDialog.dismiss()
            //call api for end trip
            callApiForEndTrip(pos, item)
        }

    }
    private fun showPopupMenu(
        view: View,
        pos: Int,
        item: DatumModel
    ) {

        var id: Int = R.menu.trips_pop_up_menu
        if (item.getSupervisorStar() == 1) id = R.menu.trips_pop_up_menu_for_supervise

        val popUpMenu = PopupMenu(activity!!, view)
        popUpMenu.menuInflater.inflate(id, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.viewDetails -> {
                    AppLogger.e("click on ViewDetails")
                    startActivity(
                        Intent(activity, TrackMapActivity::class.java)
                            .putExtra(
                                "tripId",
                                item.getTripId()
                            ).putExtra(
                                "groupId",
                                item.getGroupId()
                            ).putExtra("isSupervisor", item.getSupervisorStar())
                    )
                }

                R.id.withdrawSupervise -> {
                    AppLogger.e("click on withdrawSupervise")
                    val mDialog = Utils.alertDialogWithTwoButton(
                        mActivity!!,
                        getString(R.string.are_you_sure_want_withdraw_supervisor)
                    )
                    mDialog.buttonYes.setOnClickListener {
                        mDialog.dismiss()
                        AppLogger.e("setOnClickListener buttonYes")
                        withdrawSupervise(pos, item, (item.getTripId()))
                    }
                }

                R.id.supervise -> {
                    AppLogger.e("click on supervise")
                    putSupervise(pos, item, (item.getTripId()))
                }
            }
            return@setOnMenuItemClickListener true
        }
        popUpMenu.show()
    }

    private fun withdrawSupervise(pos: Int, item: DatumModel, tripId: Int) {
        val url: String = ApiEndPoint.WITHDRAW_SUPERVISOR + tripId
        AppLogger.e("Api url $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .withdrawSupervisor(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
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
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callApiForUpdateAdapterData(pos, item, false, false)
                        Utils.alertDialog(mActivity!!, mResponse.getMessage() ?: "")
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun callApiForUpdateAdapterData(
        pos: Int,
        item: DatumModel,
        isTripStarted: Boolean,
        isTripEnd: Boolean
    ) {
        val groupId = (activity as GroupDetailsActivity).mViewModel.groupId.value
        val url: String = ApiEndPoint.GROUPS_DETAILS + groupId
        AppLogger.e("Api url $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .getGroupDetails(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<GroupDetailsResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: GroupDetailsResponse) {
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        if (isTripEnd)
                            getTripsList(mResponse.data?.trips)
                        else
                            updateSuperviseTrips(pos, item, mResponse.data?.trips, isTripStarted)
                        hideLoading()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun putSupervise(pos: Int, item: DatumModel, tripId: Int) {
        val url: String = ApiEndPoint.PUT_SUPERVISOR + tripId
        AppLogger.e("Api url $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .putSupervisor(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
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
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callApiForUpdateAdapterData(pos, item, false, false)
                        Utils.alertDialog(mActivity!!, mResponse.getMessage() ?: "")
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun callApiForStartTrip(pos: Int, item: DatumModel) {
        val url: String = ApiEndPoint.START_TRIP + item.getTripId()
        AppLogger.e("Api url $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .putTripStart(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
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
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callApiForUpdateAdapterData(pos, item, true, false)

                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun callApiForEndTrip(pos: Int, item: DatumModel) {
        val url: String = ApiEndPoint.END_TRIP + item.getTripId()
        AppLogger.e("Api url $url")
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .putTripEnd(url, mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
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
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        callApiForUpdateAdapterData(pos, item, false, true)
                        Utils.alertDialog(mActivity!!, mResponse.getMessage() ?: "")
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun updateSuperviseTrips(
        pos: Int,
        item: DatumModel,
        trips: ArrayList<DatumModel>?,
        tripStarted: Boolean
    ) {

        item.setStatus(trips!![pos].getStatus())
        item.setSupervisorStar(trips!![pos].getSupervisorStar())
        item.setSupervisorName(trips[pos].getSupervisorName())
        mAdapter?.notifyItemChanged(pos)
        if (tripStarted) {
            startActivity(
                Intent(activity, TrackMapActivity::class.java).putExtra(
                    "tripId",
                    item.getTripId()
                ).putExtra(
                    "groupId",
                    item.getGroupId()
                ).putExtra("isSupervisor", item.getSupervisorStar())
            )
        }
    }

}
