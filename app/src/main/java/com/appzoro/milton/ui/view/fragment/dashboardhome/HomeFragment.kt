package com.appzoro.milton.ui.view.fragment.dashboardhome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.HomePageResponse
import com.appzoro.milton.network.ErrorFailure
import com.appzoro.milton.network.RetrofitService
import com.appzoro.milton.network.UtilThrowable
import com.appzoro.milton.ui.view.activity.notification.NotificationsActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.dashboard_fragment_header.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : BaseFragment() {
    private var param1: String? = null
    private var param2: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var mAdapter: HomeAdapter? = null
    private var mPreferences: PreferenceManager? = null
    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.imageViewHamburger.setOnClickListener {
            Utils.drawerOpenClose(drawerLayout = activity!!.drawer_layout)
        }
        view.imageNotification.setOnClickListener {
            startActivity(Intent(activity, NotificationsActivity::class.java))
        }
        return view
    }

    override fun setUp(view: View) {
        mView = view

        mPreferences = PreferenceManager(activity!!)
        mAdapter = HomeAdapter()
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        view.recyclerViewHome?.layoutManager = mLayoutManager
        view.recyclerViewHome?.adapter = mAdapter

    }

    private fun setProfileDetails(mData: HomePageResponse.Data?) {
        if (mView != null) {
            try {
                val profileTitle = getString(R.string.hi)+" " + mData?.parentData?.fullName
                mView?.tvProfileTitle?.text = profileTitle
                val description =
                    getString(R.string.your_kids_has_taken) + " " + mData?.tripsWalked + " " + getString(
                        R.string.trips_this_week
                    )
                mView?.tvDescription?.text = description
                val image = mData?.parentData?.image ?: ""
                if (activity != null)
                    Utils.loadImage(activity!!, image, view?.ivProfile!!)
            } catch (e: Exception) {
                Log.d(Constant.TAG, e.message.toString())
            }
        }
    }

    private fun getHomePage() {

        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(activity!!).getApi()
            .getHomePage(mPreferences?.getString(Constant.authToken) ?: "")
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, activity!!)
            }
            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : io.reactivex.Observer<HomePageResponse> {
                    override fun onComplete() {
                        AppLogger.e(Constant.TAG + "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(mListResponse: HomePageResponse) {
                        AppLogger.e(Constant.TAG + Gson().toJson(mListResponse))
                        if (mListResponse.data != null) {
                            if (mListResponse.data?.supervisorTrips == null)
                                mListResponse.data?.supervisorTrips = ArrayList()
                            if (mListResponse.data?.studentTrips != null)
                                for (value in mListResponse.data?.studentTrips!!.withIndex()) {
                                    val trip = value.value
                                    trip.isSupervisor = false
                                    mListResponse.data?.supervisorTrips?.add(trip)
                                }
                            setProfileDetails(mListResponse.data!!)
                            mAdapter?.setList(activity!!, mListResponse.data?.supervisorTrips)
                            if (mListResponse.data?.supervisorTrips?.size ?: 0 > 0) view?.tvNoData?.visibility =
                                View.GONE else view?.tvNoData?.visibility = View.VISIBLE
                        } else {
                            mAdapter?.setList(activity!!, ArrayList())
                            view?.tvNoData?.visibility = View.VISIBLE
                        }
                        mAdapter?.notifyDataSetChanged()
                        hideLoading()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        Utils.alertDialog(activity!!, temError?.mMessage ?: "")
                        mAdapter?.setList(activity!!, ArrayList())
                        mAdapter?.notifyDataSetChanged()
                        view?.tvNoData?.visibility = View.VISIBLE
                        e.printStackTrace()
                    }
                })

    }

    override fun onResume() {
        getHomePage()
        super.onResume()
    }

}