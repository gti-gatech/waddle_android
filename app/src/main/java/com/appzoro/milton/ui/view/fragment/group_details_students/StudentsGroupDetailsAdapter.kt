package com.appzoro.milton.ui.view.fragment.group_details_students

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.ui.view.activity.onboarding.addstudent.AddStudentActivity
import com.appzoro.milton.utility.AppLogger
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import java.util.*

class StudentsGroupDetailsAdapter(val mActivity: Activity) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<DatumModel>

    init {
        mList = ArrayList()
    }

    internal fun setList(mList: ArrayList<DatumModel>?) {
        AppLogger.e("mList Size ${mList?.size ?: 0}")
        if (mList != null) this.mList = mList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_student_group_item_list_normal, parent, false)
            return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) {
              setViewHolder(holder, position)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {

        mViewHolder.tvStudent.text = mList[position].getFullName()
        mViewHolder.tvGrade.text = "Grade: " + mList[position].getGrade()
        mViewHolder.tvStop.text = "Default Stop: " + mList[position].getStopName()

    }


    internal inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mCardView: MaterialCardView = view.findViewById(R.id.mCardView)
        val tvGrade: MaterialTextView = view.findViewById(R.id.tvGrade)
        val tvStop: MaterialTextView = view.findViewById(R.id.tvStop)
        val tvStudent: MaterialTextView = view.findViewById(R.id.textViewStudentName)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
