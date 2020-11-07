package com.appzoro.milton.ui.view.activity.dashboard.navtrips

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivityTripsBinding
import com.appzoro.milton.model.TripsResponse
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_trips.*
import kotlinx.android.synthetic.main.layout_header.*

class TripsActivity : BaseActivity() {

    private var mActivity: Activity? = null
    private var mPreferences: PreferenceManager? = null
    private var mAdapterUpcoming: TripsAdapter? = null
    private var mAdapterHistory: TripsAdapter? = null
    private lateinit var dataBinding: ActivityTripsBinding
    private lateinit var mViewModel: TripsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_trips)
        mViewModel = ViewModelProviders.of(this).get(TripsViewModel::class.java)
        dataBinding.tripsViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        textViewTitle.text = getString(R.string.trips)
        imageViewBack.setOnClickListener {
            startActivity(
                Intent(this, DashBoardActivity::class.java).putExtra(
                    "comeFrom",
                    ""
                )
            )
        }

        mPreferences = PreferenceManager(this)
        mActivity = this@TripsActivity
        mAdapterUpcoming = TripsAdapter()
        mUpcomingRecyclerView?.adapter = mAdapterUpcoming
        mAdapterHistory = TripsAdapter()
        mHistoryRecyclerView?.adapter = mAdapterHistory
        callApiForGetTrips()

    }

    private fun callApiForGetTrips() {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(this).getApi()
            .getTripsHistory(mPreferences?.getString(Constant.authToken).toString())
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, this)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<TripsResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mResponse: TripsResponse) {
                        hideLoading()
                        AppLogger.e(Constant.TAG + Gson().toJson(mResponse))
                        manageTripsData(mResponse.data)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun manageTripsData(data: TripsResponse.Data?) {

        if (data != null) {
            val studentsUpcoming = data.upcoming?.studentsUpcoming ?: ArrayList()
            val supervisorUpcoming = data.upcoming?.supervisorUpcoming ?: ArrayList()
            supervisorUpcoming.addAll(studentsUpcoming)
            val studentsHistory = data.history?.studentsHistory
            val studentsHistoryToday = studentsHistory?.today ?: ArrayList()
            val studentsHistoryYesterday = studentsHistory?.yesterday ?: ArrayList()
            val studentsHistoryPrevious = studentsHistory?.previous ?: ArrayList()
            val supervisorHistory = data.history?.supervisorHistory
            val supervisorHistoryToday = supervisorHistory?.today ?: ArrayList()
            val supervisorHistoryYesterday = supervisorHistory?.yesterday ?: ArrayList()
            val supervisorHistoryPrevious = supervisorHistory?.previous ?: ArrayList()

            supervisorHistoryToday.addAll(supervisorHistoryYesterday)
            supervisorHistoryToday.addAll(supervisorHistoryPrevious)
            supervisorHistoryToday.addAll(studentsHistoryToday)
            supervisorHistoryToday.addAll(studentsHistoryYesterday)
            supervisorHistoryToday.addAll(studentsHistoryPrevious)

            mAdapterUpcoming?.setList(supervisorUpcoming)
            mAdapterUpcoming?.notifyDataSetChanged()
            mAdapterHistory?.setList(supervisorHistoryToday)
            mAdapterHistory?.notifyDataSetChanged()

            if (supervisorUpcoming.size > 0) tvNoUpcomingData.visibility =
                View.GONE else tvNoUpcomingData.visibility = View.VISIBLE

            if (supervisorHistoryToday.size > 0) tvNoHistoryData.visibility =
                View.GONE else tvNoHistoryData.visibility = View.VISIBLE

        } else {
            tvNoUpcomingData.visibility = View.VISIBLE
            tvNoHistoryData.visibility = View.VISIBLE
        }
    }

}