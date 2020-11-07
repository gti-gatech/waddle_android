package com.appzoro.milton.ui.view.activity.onboarding.searchgroup

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.GroupModelData
import com.appzoro.milton.ui.ItemClick
import com.appzoro.milton.utility.Utils

class SearchGroupAdapter(
    private val mContext: Activity,
    private var studentListData: ArrayList<GroupModelData>,
    private val mListener: ItemClick
) :
    RecyclerView.Adapter<SearchGroupAdapter.SearchGroupViewHolder>() {

    class SearchGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage = itemView.findViewById(R.id.imageViewProfile) as ImageView
        val title = itemView.findViewById(R.id.textViewTitle) as TextView
        val lastWalk = itemView.findViewById(R.id.textViewLastWalk) as TextView
        val studentCount = itemView.findViewById(R.id.textViewStudentCount) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchGroupViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.group_list_item, parent, false)
        return SearchGroupViewHolder(view)
    }

    fun filterList(filterName: ArrayList<GroupModelData>) {
        this.studentListData = filterName
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return studentListData.size
    }

    override fun onBindViewHolder(holder: SearchGroupViewHolder, position: Int) {
        Utils.loadImage(
            mActivity = mContext,
            ivImageView = holder.profileImage,
            path = studentListData[position].image
        )
        val itemValue = studentListData[position]
        holder.title.text = itemValue.groupName
        val studentCount = "Student : ${itemValue.totalStudents}"
        holder.studentCount.text = studentCount
        val lastWalkMsg = "Date: " + Utils.getDateFromServerTime(itemValue.onCreatedTime)
        holder.lastWalk.text = lastWalkMsg
        holder.itemView.setOnClickListener {
            mListener.onItemClick(position)
        }


    }
}