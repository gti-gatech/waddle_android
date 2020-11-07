package com.appzoro.milton.ui.view.fragment.dashboardmessage

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.MessageModel
import com.appzoro.milton.ui.view.activity.message_inbox.MessageInboxActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Utils
import com.google.android.material.textview.MaterialTextView
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class MessagesAdapter(val mActivity: Activity) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<MessageModel>

    init {
        mList = ArrayList()
    }

    internal fun setList(mList: ArrayList<MessageModel>?) {
        AppLogger.e("mList Size ${mList?.size ?: 0}")
        if (mList != null) {
            this.mList = mList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_messages, parent, false)
        return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) {
            setViewHolder(holder, position)
        }
    }

    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {

        val image = mList[position].image ?: ""
        Utils.loadImage(mActivity, image, mViewHolder.ivProfile, mViewHolder.mProgress)

        mViewHolder.tvName.text = mList[position].groupName
        mViewHolder.tvMessage.text = mList[position].message

        val date = Utils.getMessageTime(mList[position].createdOn ?: "")?:""
        mViewHolder.tvDate.text = date //2020-08-19 12:22:17

        if (mList[position].totalUnRead ?: 0 > 0) {
            mViewHolder.tvMessage.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark))
            mViewHolder.tvMessageCount.text = (mList[position].totalUnRead ?: 0).toString()
            mViewHolder.tvMessageCount.visibility = View.VISIBLE
        } else {
            mViewHolder.tvMessageCount.visibility = View.GONE
            mViewHolder.tvMessage.setTextColor(ContextCompat.getColor(mActivity, R.color.colorText))
        }

        mViewHolder.mLayout.setOnClickListener{
            mActivity.startActivity(MessageInboxActivity.NewInstance.getIntent(mActivity, mList[position]))
        }

    }

    internal class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mLayout: LinearLayout = view.findViewById(R.id.mLayout)
        val ivProfile: CircleImageView = view.findViewById(R.id.ivProfile)
        val mProgress: ProgressBar = view.findViewById(R.id.mProgress)
//        val mLayoutDot: LinearLayout = view.findViewById(R.id.mLayoutDot)
        val tvName: MaterialTextView = view.findViewById(R.id.tvName)
        val tvMessage: MaterialTextView = view.findViewById(R.id.tvMessage)
        val tvMessageCount: MaterialTextView = view.findViewById(R.id.tvMessageCount)
        val tvDate: MaterialTextView = view.findViewById(R.id.tvDate)

    }

    override fun getItemCount(): Int {
//        return 10
        return mList.size
    }

}
