package com.appzoro.milton.ui.view.activity.trackmap

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.GetMapTrackResponse
import com.appzoro.milton.ui.ItemClick
import com.appzoro.milton.ui.view.PickStatus
import com.google.android.material.textview.MaterialTextView


class TrackItemAdapter(
    val mContext: Context,
    val mListener: ItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<GetMapTrackResponse.Data.Datum>
    private var tripStarted: Boolean
    private var isSupervisor: Int

    init {
        mList = ArrayList()
        tripStarted = false
        isSupervisor = 0
    }

    internal fun setList(
        mList: ArrayList<GetMapTrackResponse.Data.Datum>,
        tripStarted: Boolean,
        isSupervisor: Int
    ) {
        this.mList = mList
        this.tripStarted = tripStarted
        this.isSupervisor = isSupervisor
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_student_row, parent, false)
            CustomViewHolderSupervisor(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_normal_student_row, parent, false)
            CustomViewHolderParents(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolderSupervisor) {
            setViewHolder(holder, position)
        }
        if (holder is CustomViewHolderParents)
            setViewHolder(holder, position)
    }


    private fun setViewHolder(mViewHolder: CustomViewHolderSupervisor, position: Int) {
        val tempItem: GetMapTrackResponse.Data.Datum = mList[position]
        mViewHolder.textViewStudentName.text = tempItem.studentName
        mViewHolder.textViewWalkedIn.text = tempItem.stopName
        if (tempItem.status == PickStatus.NOT_PICKED.toString()) mViewHolder.imageViewDone.setImageResource(
            R.drawable.ic_uncheck
        ) else
            mViewHolder.imageViewDone.setImageResource(R.drawable.ic_check_circle)
        mViewHolder.imageViewCall.setOnClickListener {
            callPhone(mContext, mList[position].contact)
        }
        mViewHolder.imageViewDone.setOnClickListener {
            if (tempItem.status == PickStatus.NOT_PICKED.toString()) {
                mListener.onItemClick(position)
            }
        }
        if (!tripStarted) {
            mViewHolder.imageViewDone.visibility = View.GONE
            mViewHolder.imageViewCall.visibility = View.GONE
            mViewHolder.textViewWalkedIn.visibility=View.VISIBLE
            mViewHolder.linearLayoutCompat.setBackgroundResource(R.drawable.edit_text_bottom_border)

        } else {
            mViewHolder.textViewWalkedIn.visibility=View.GONE
            mViewHolder.linearLayoutCompat.setBackgroundResource(R.drawable.edit_text_bottom_border_less)
            mViewHolder.imageViewDone.visibility = View.VISIBLE
            mViewHolder.imageViewCall.visibility = View.VISIBLE
        }
    }

    private fun setViewHolder(mViewHolder: CustomViewHolderParents, position: Int) {
        val tempItem = mList[position]
        mViewHolder.textViewRouteName.text = tempItem.stopName
            mViewHolder.textViewDistance.visibility = View.VISIBLE
            mViewHolder.textViewTime.visibility = View.VISIBLE
            val travelDistance = "Distance : ${tempItem.travelDistance}"
            val travelTime = "Time : ${tempItem.travelTime}"
            mViewHolder.textViewDistance.text = travelDistance
            mViewHolder.textViewTime.text = travelTime
        if (mList[position].status == PickStatus.NOT_PICKED.toString())
            mViewHolder.imageViewStatus.setColorFilter(Color.WHITE)
        else
            mViewHolder.imageViewStatus.setColorFilter(Color.GREEN)

    }

    internal class CustomViewHolderSupervisor(view: View) : RecyclerView.ViewHolder(view) {
        val linearLayoutCompat: ConstraintLayout = view.findViewById(R.id.linearLayoutCompat)
        val textViewWalkedIn: MaterialTextView = view.findViewById(R.id.textViewWalkedIn)
        val textViewStudentName: MaterialTextView = view.findViewById(R.id.textViewStudentName)
        val imageViewCall: ImageView = view.findViewById(R.id.imageViewCall)
        val imageViewDone: ImageView = view.findViewById(R.id.imageViewDone)


    }

    internal class CustomViewHolderParents(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewStatus: ImageView = view.findViewById(R.id.imageViewDoneNormal)
        val textViewRouteName: MaterialTextView = view.findViewById(R.id.textViewRouteName)
        val textViewDistance: MaterialTextView = view.findViewById(R.id.textViewDistance)
        val textViewTime: MaterialTextView = view.findViewById(R.id.textViewTime)

    }

    private fun callPhone(mContext: Context, contact: String?) {
        val number: Uri = Uri.parse("tel:$contact")
        val callIntent = Intent(Intent.ACTION_DIAL, number)
        mContext.startActivity(callIntent)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
