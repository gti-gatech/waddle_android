package com.appzoro.milton.ui.view.fragment.dashboardhome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.HomePageResponse
import com.appzoro.milton.ui.view.activity.group_details.GroupDetailsActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Utils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import java.util.*

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList: ArrayList<HomePageResponse.Data.StudentTrips>
    private var mActivity: Activity? = null

    init {
        mList = ArrayList()
    }

    internal fun setList(
        mActivity: Activity,
        mList: ArrayList<HomePageResponse.Data.StudentTrips>?
    ) {
        AppLogger.e("mList Size ${mList?.size ?: 0}")
        this.mActivity = mActivity
        if (mList != null) {
            this.mList = mList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 1) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_item_list_supervisor, parent, false)
            HomeViewHolderSupervisor(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.home_item_list, parent, false)
            CustomViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].isSupervisor) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) {
            setViewHolder(holder, position)
        } else if (holder is HomeViewHolderSupervisor) {
            setViewHolder(holder, position)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {

        mViewHolder.tvStudentName.text = "Student: " + mList[position].studentName
        mViewHolder.tvDate.text = "Date: " + Utils.getDateFromServerTime(mList[position].dueOn)
//        mViewHolder.tvStop.text = "Pick up Stop: " + mList[position].groupName
        mViewHolder.tvStop.text = mList[position].groupName
        mViewHolder.tvSupervisorName.text = "Supervisor: " + mList[position].supervisorName

        mViewHolder.mCardView.setOnClickListener {
            mActivity?.startActivity(
                Intent(mActivity, GroupDetailsActivity::class.java)
                    .putExtra("groupId", mList[position].groupId)
                    .putExtra("comeFrom", "group")
            )
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setViewHolder(mViewHolder: HomeViewHolderSupervisor, position: Int) {
        val date = "Date: " + Utils.getDateFromServerTime(mList[position].dueOn)
        mViewHolder.tvDate.text = date
        mViewHolder.tvStop.text = mList[position].groupName
//        mViewHolder.tvStop.text = "Pick up Stop: " + mList[position].groupName
        mViewHolder.tvSupervisorName.text = "Supervisor: " + mList[position].supervisorName

        mViewHolder.mCardView.setOnClickListener {
            mActivity?.startActivity(
                Intent(mActivity, GroupDetailsActivity::class.java)
                    .putExtra("groupId", mList[position].groupId)
                    .putExtra("comeFrom", "group")
            )
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    internal inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mCardView: MaterialCardView = view.findViewById(R.id.mCardView)
        val tvStudentName: MaterialTextView = view.findViewById(R.id.textViewStudentName)
        val tvDate: MaterialTextView = view.findViewById(R.id.textViewDate)
        val tvStop: MaterialTextView = view.findViewById(R.id.textViewStop)
        val tvSupervisorName: MaterialTextView = view.findViewById(R.id.textViewSuperVisorName)
    }

    internal inner class HomeViewHolderSupervisor(view: View) : RecyclerView.ViewHolder(view) {
        val mCardView: MaterialCardView = view.findViewById(R.id.mCardView)
        val tvDate: MaterialTextView = view.findViewById(R.id.textViewDate)
        val tvStop: MaterialTextView = view.findViewById(R.id.textViewStop)
        val tvSupervisorName: MaterialTextView = view.findViewById(R.id.textViewSuperVisorName)
    }

}
