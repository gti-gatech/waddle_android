package com.appzoro.milton.ui.view.fragment.group_details_students

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.model.GroupDetailsResponse
import com.appzoro.milton.utility.AppLogger
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_student_group.*
import kotlinx.android.synthetic.main.fragment_group_details_students.view.*
import kotlinx.android.synthetic.main.fragment_group_details_students.view.mRecyclerView
import kotlinx.android.synthetic.main.fragment_group_details_students.view.tvNoData
import kotlinx.android.synthetic.main.fragment_group_details_trips.view.*

class GroupDetailsStudentsFragment : BaseFragment() {

    lateinit var mSharedPreferences: PreferenceManager
    private var mAdapter: StudentsGroupDetailsAdapter? = null

    object NewInstance {
        fun instance(): GroupDetailsStudentsFragment {
            val fragment =
                GroupDetailsStudentsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var mView: View? = null
    private var mActivity: Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_group_details_students, container, false)
        mView = root
        return root
    }

    override fun setUp(view: View) {
        mActivity = activity
        mSharedPreferences = PreferenceManager(mActivity!!)

        view.mRecyclerView?.setHasFixedSize(false)
        mAdapter = StudentsGroupDetailsAdapter(mActivity!!)
        view.mRecyclerView?.adapter = mAdapter

    }

    fun getStudentsList(groupStudents: ArrayList<DatumModel>?) {
        AppLogger.e("groupStudents ${Gson().toJson(groupStudents)}")
        mAdapter?.setList(groupStudents)
        mAdapter?.notifyDataSetChanged()

        if (groupStudents?.size ?: 0 > 0) view?.tvNoData?.visibility = View.GONE else view?.tvNoData?.visibility = View.VISIBLE
    }

}
