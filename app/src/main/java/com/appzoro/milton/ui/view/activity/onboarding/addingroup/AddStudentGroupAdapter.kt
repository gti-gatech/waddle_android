package com.appzoro.milton.ui.view.activity.onboarding.addingroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.CommonListResponse
import com.appzoro.milton.ui.OnClickInterface

class AddStudentGroupAdapter(
    private val studentListData: CommonListResponse,
    private val mListener: OnClickInterface
) :
    RecyclerView.Adapter<AddStudentGroupAdapter.AddStudentViewHolder>() {
//    private var index: Int = -1

    class AddStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById(R.id.textViewName) as TextView
        val schoolName = itemView.findViewById(R.id.textViewSchoolName) as TextView
        val imageCheck = itemView.findViewById(R.id.imageViewCheck) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddStudentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.student_list_item, parent, false)
        return AddStudentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return studentListData.getData()!!.size
    }

    override fun onBindViewHolder(holder: AddStudentViewHolder, position: Int) {
        val itemValue = studentListData.getData()!![position]
        holder.name.text = itemValue.getFullName()
        holder.schoolName.text = itemValue.getGrade()
        if (itemValue.isItemCheck)
            holder.imageCheck.setImageResource(R.drawable.ic_check_circle)
        else
            holder.imageCheck.setImageResource(R.drawable.ic_uncheck)

        holder.itemView.setOnClickListener {
            itemValue.isItemCheck = !itemValue.isItemCheck
            notifyDataSetChanged()
            mListener.onClickItem(position, itemValue.isItemCheck)

        }


    }
}