package com.appzoro.milton.ui.view.fragment.group_my_groups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.ui.view.fragment.dashboardschedule.OnThreeDotCLick
import com.appzoro.milton.utility.Utils
import com.google.android.material.textview.MaterialTextView

class MyGroupsAdapter(val mContext: Context, val mListener: OnThreeDotCLick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<DatumModel>

    init {
        mList = ArrayList()
    }

    internal fun setList(mList: ArrayList<DatumModel>) {
        this.mList = mList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_my_group_item_list_supervisor, parent, false)
        return CustomViewHolderSupervisor(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolderSupervisor) setViewHolder(holder, position)
    }


    private fun setViewHolder(mViewHolder: CustomViewHolderSupervisor, position: Int) {
        Utils.loadImage(
            mActivity = mContext,
            ivImageView = mViewHolder.ivProfile,
            path = mList[position].getImage()
        )
        mViewHolder.tvGroupName.text = mList[position].getGroupName()
        var upcomingDate = ""
        if (mList[position].getDueOn().isNullOrEmpty()) {
            upcomingDate =
                "No upcoming trips for now"
        } else
            upcomingDate =
                mContext.getString(R.string.upcoming_dates) + Utils.getDateFromServerTime(mList[position].getDueOn())
        mViewHolder.tvDate.text = upcomingDate
        val totalTrips = "Total trips walked: ${mList[position].getTripsWalked()}"
        mViewHolder.tvTotalTrips.text = totalTrips
        val totalStudentCount = "Student: ${mList[position].getTotalStudents()}"
        mViewHolder.tvStudent.text = totalStudentCount
        if (mList[position].getSupervisorStar() == 0)
            mViewHolder.tvSupervisor.visibility = View.GONE
        else
            mViewHolder.tvSupervisor.visibility = View.VISIBLE

        //0-for pop up menu
        //1-for item click
        mViewHolder.ivEditSchedule.setOnClickListener {
            mListener.onItemClick(mViewHolder.ivEditSchedule, mViewHolder.adapterPosition, 0)
        }
        mViewHolder.itemView.setOnClickListener {
            mListener.onItemClick(mViewHolder.ivEditSchedule, mViewHolder.adapterPosition, 1)
        }

    }

    internal inner class CustomViewHolderSupervisor(view: View) : RecyclerView.ViewHolder(view) {
        val tvSupervisor: MaterialTextView = view.findViewById(R.id.tvSupervisor)
        val ivProfile: ImageView = view.findViewById(R.id.ivProfile)
        val tvGroupName: MaterialTextView = view.findViewById(R.id.tvGroupName)
        val tvDate: MaterialTextView = view.findViewById(R.id.tvDate)
        val tvTotalTrips: MaterialTextView = view.findViewById(R.id.tvTotalTrips)
        val tvStudent: MaterialTextView = view.findViewById(R.id.tvStudent)
        val ivEditSchedule: AppCompatImageView = view.findViewById(R.id.ivEditSchedule)

    }


    override fun getItemCount(): Int {
        return mList.size
    }

}
