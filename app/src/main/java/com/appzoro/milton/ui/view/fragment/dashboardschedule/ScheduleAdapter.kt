package com.appzoro.milton.ui.view.fragment.dashboardschedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.GetScheduleResponse
import com.appzoro.milton.utility.AppLogger
import com.google.android.material.textview.MaterialTextView
import java.util.*

class ScheduleAdapter(private val mListener: OnClickScheduleInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList: ArrayList<GetScheduleResponse.Datum>
    private var isSupervisor: Boolean = false

    init {
        mList = ArrayList()
    }

    internal fun setList(isSupervisor: Boolean, mList: ArrayList<GetScheduleResponse.Datum>) {
        AppLogger.e("mList Size ${mList.size}")
        this.isSupervisor = isSupervisor
        this.mList = mList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_schedule_task, parent, false)
        return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) {
            setViewHolder(holder, position)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {

        mViewHolder.tvTime.text = (mList[position].displayTime?:"")
        mViewHolder.tvMessage.text = (mList[position].groupName?:"")
        mViewHolder.tvStudent.text = "Student: ${mList[position].studentName?:""}"
        mViewHolder.tvSupervisor.text = "Supervisor: ${mList[position].supervisorName?:""}"
        if (isSupervisor) {
            mViewHolder.tvStudent.visibility = View.GONE
            mViewHolder.tvSupervisorStar.visibility = View.VISIBLE
        }else{
            mViewHolder.tvStudent.visibility = View.VISIBLE
            mViewHolder.tvSupervisorStar.visibility = View.GONE
        }
        mViewHolder.ivEditSchedule.setOnClickListener {
            mListener.onItemClickItem(mViewHolder.ivEditSchedule, mViewHolder.adapterPosition, mList[mViewHolder.adapterPosition])
        }
    }

    internal inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvSupervisorStar: MaterialTextView = view.findViewById(R.id.tvSupervisorStar)
        val tvTime: MaterialTextView = view.findViewById(R.id.tvTime)
        val tvMessage: MaterialTextView = view.findViewById(R.id.tvMessage)
        val tvStudent: MaterialTextView = view.findViewById(R.id.tvStudent)
        val tvSupervisor: MaterialTextView = view.findViewById(R.id.tvSupervisor)
        val ivEditSchedule: AppCompatImageView = view.findViewById(R.id.ivEditSchedule)
       // val mLayout: LinearLayout = view.findViewById(R.id.mLayout)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
