package com.appzoro.milton.ui.view.activity.message_inbox

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.MessageModel
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Utils
import com.google.android.material.textview.MaterialTextView
import java.util.*

class MessageInboxAdapter(val mActivity: Activity, private val parentId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList: ArrayList<MessageModel> = ArrayList()

    init {
        mList = ArrayList()
    }

    fun setList(mList: ArrayList<MessageModel>?) {
        AppLogger.e("mList Size ${mList?.size ?: 0}")
        if (mList != null) {
            this.mList = mList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_message_inbox_right, parent, false)
            CustomViewHolderLeft(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_message_inbox_left, parent, false)
            CustomViewHolderRight(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CustomViewHolderLeft) {
            setViewHolderLeft(holder, position)
        } else {
            setViewHolderRight(holder as CustomViewHolderRight, position)
        }

    }

    override fun getItemViewType(position: Int): Int {

//        AppLogger.e("getItemViewType parentId $parentId")

        return if (parentId == mList[position].senderId) {
            1//right side card
        } else {
            0//left side card
        }

    }

    private fun setViewHolderLeft(mViewHolder: CustomViewHolderLeft, position: Int) {

        mViewHolder.tvMessage.text = mList[position].message ?: ""
        val date = mList[position].senderName + ", " + Utils.getChattingMessageTime(
            mList[position].createdOn ?: ""
        ) ?: ""
        mViewHolder.tvDate.text = date

    }

    private fun setViewHolderRight(mViewHolder: CustomViewHolderRight, position: Int) {
        mViewHolder.tvMessage.text = mList[position].message ?: ""
        val date = mList[position].senderName + ", " + Utils.getChattingMessageTime(
            mList[position].createdOn ?: ""
        ) ?: ""
        mViewHolder.tvDate.text = date
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    internal class CustomViewHolderLeft(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: MaterialTextView = view.findViewById(R.id.tvMessage)
        val tvDate: MaterialTextView = view.findViewById(R.id.tvDate)
    }

    internal class CustomViewHolderRight(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: MaterialTextView = view.findViewById(R.id.tvMessage)
        val tvDate: MaterialTextView = view.findViewById(R.id.tvDate)
    }

}
