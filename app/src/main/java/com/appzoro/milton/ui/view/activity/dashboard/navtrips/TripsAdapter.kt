package com.appzoro.milton.ui.view.activity.dashboard.navtrips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.android.material.textview.MaterialTextView
import java.util.*

class TripsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<DatumModel>

    init {
        mList = ArrayList()
    }

    fun setList(mList: ArrayList<DatumModel>?) {
        AppLogger.e("mList Size ${mList?.size ?: 0}")
        if (mList != null) {
            this.mList = mList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_trips_item_list, parent, false)
        return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) {
            setViewHolder(holder, position)
        }
    }

    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {
        val date = "Date: " + Utils.getDateFromServerTime(mList[position].getDueOn())
        val pickupStop: String
        if (mList[position].getStopName() != null)
            pickupStop = "Pickup Stop: ${mList[position].getStopName()}"
        else
            pickupStop = "Pickup Stop: ${Constant.emptyString}"

        val supervisor = "Supervisor: ${mList[position].getFullName()}"
        val student = "Student: ${mList[position].getStudentName()}"

        mViewHolder.tvGroupDate.text = date
        mViewHolder.tvGroupName.text = mList[position].getGroupName()
        mViewHolder.tvPickupStop.text = pickupStop
        mViewHolder.tvSupervisorName.text = supervisor
        mViewHolder.tvStudent.text = student

        val isSupervisorStar = (mList[position].getSupervisorStar() ?: 0)
        if (isSupervisorStar == 1) {
            mViewHolder.tvSupervisorStar.visibility = View.VISIBLE
            mViewHolder.tvStudent.visibility = View.GONE
        } else {
            mViewHolder.tvSupervisorStar.visibility = View.GONE
            mViewHolder.tvStudent.visibility = View.VISIBLE
        }
    }

    internal inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvSupervisorStar: MaterialTextView = view.findViewById(R.id.tvSupervisorStar)
        val tvGroupDate: MaterialTextView = view.findViewById(R.id.tvGroupDate)
        val tvGroupName: MaterialTextView = view.findViewById(R.id.tvGroupName)
        val tvPickupStop: MaterialTextView = view.findViewById(R.id.tvPickupStop)
        val tvSupervisorName: MaterialTextView = view.findViewById(R.id.tvSupervisorName)
        val tvStudent: MaterialTextView = view.findViewById(R.id.tvStudent)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
