package com.appzoro.milton.ui.view.fragment.dashboardstudent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.model.GetScheduleResponse
import com.appzoro.milton.ui.view.activity.onboarding.addstudent.AddStudentActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Utils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class StudentsAdapter(val mActivity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList: ArrayList<DatumModel>

    init {
        mList = ArrayList()
    }

    internal fun setList(mList: ArrayList<DatumModel>) {
        AppLogger.e("mList Size ${mList.size}")
        this.mList = mList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_students, parent, false)
        return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) setViewHolder(holder, position)
    }

    @SuppressLint("SetTextI18n")
    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {

        val itemValue = mList[position]
        val mClassYear = "Class of " + Utils.getYearFromServerTime(itemValue.getCreatedOn()?:"")
        val imageUrl = (itemValue.getImage()?:"")

        mViewHolder.tvStudentName.text = itemValue.getFullName()
        mViewHolder.tvSchoolName.text = itemValue.getSchoolName()
        mViewHolder.tvClass.text = mClassYear

        Utils.loadImage(mActivity = mActivity,
            ivImageView = mViewHolder.ivProfile, path = imageUrl)

        mViewHolder.mCardView.setOnClickListener {
            mActivity.startActivityForResult(Intent(mActivity, AddStudentActivity::class.java)
                .putExtra("studentItem", itemValue)
                .putExtra("comeFrom","edit"),1234)
        }
    }

    internal class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProfile: CircleImageView = view.findViewById(R.id.ivProfile)
        val tvStudentName: MaterialTextView = view.findViewById(R.id.tvStudentName)
        val tvSchoolName: MaterialTextView = view.findViewById(R.id.tvSchoolName)
        val tvClass: MaterialTextView = view.findViewById(R.id.tvClass)
        val mCardView: MaterialCardView = view.findViewById(R.id.mCardView)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
