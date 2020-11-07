package com.appzoro.milton.ui.view.fragment.dashboardstudent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.appzoro.milton.ui.view.activity.onboarding.addstudent.AddStudentActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.dashboard_fragment_header.view.*
import kotlinx.android.synthetic.main.fragment_student.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var param1: String? = null
    private var param2: String? = null

    private var mPreferences: PreferenceManager? = null
    private var mAdapter: StudentsAdapter? = null
    private var mActivity: Activity? = null

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
        val view = inflater.inflate(R.layout.fragment_student, container, false)
        view.imageViewHamburger.setOnClickListener {
            Utils.drawerOpenClose(drawerLayout = activity!!.drawer_layout)
        }
        return view

    }

    override fun setUp(view: View) {
        view.imageNotification.isVisible = false
        mActivity = activity
        mPreferences = PreferenceManager(mActivity!!)
        mAdapter = StudentsAdapter(mActivity!!)
        view.mRecyclerView?.adapter = mAdapter

        view.mFloatingButton.setOnClickListener {
            startActivityForResult(
                Intent(activity, AddStudentActivity::class.java)
                    .putExtra("comeFrom", "student"), 1234
            )
        }

        fetchStudentListData()

    }

    private fun fetchStudentListData() {
        showLoading()
        var temError: ErrorFailure? = null
        RetrofitService.getInstance(mActivity!!).getApi()
            .getStudentList(ApiEndPoint.STUDENT_LIST,
                (mPreferences?.getString(Constant.authToken) ?: "")
            )
            .doOnError {
                temError = UtilThrowable.mCheckThrowable(it, mActivity!!)
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

                        if(mListResponse.getData() != null) {
                            mAdapter?.setList(mListResponse.getData()!!)
                            mAdapter?.notifyDataSetChanged()
                            setStudentDataList(mListResponse.getData())
                        }
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        setStudentDataList(null)
                        Utils.alertDialog(mActivity!!, temError?.mMessage ?: "")
                        e.printStackTrace()
                    }

                })
    }

    private fun setStudentDataList(dataList: ArrayList<DatumModel>?) {
        if (dataList != null) {
            mAdapter?.setList(dataList)
            if(dataList.size>0) view?.tvNoData?.visibility = View.GONE else view?.tvNoData?.visibility = View.VISIBLE
        } else {
            mAdapter?.setList(ArrayList())
            view?.tvNoData?.visibility = View.VISIBLE
        }
        mAdapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogger.e("Fragment Student requestCode  $requestCode resultCode $resultCode")
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            fetchStudentListData()
        }
    }

}