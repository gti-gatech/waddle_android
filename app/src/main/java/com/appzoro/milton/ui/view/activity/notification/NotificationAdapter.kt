package com.appzoro.milton.ui.view.activity.notification

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.model.NotificationsListResponse
import com.appzoro.milton.utility.Utils
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(private val mListener: OnClickItemInterface, val mActivity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mViewBinderHelper: ViewBinderHelper = ViewBinderHelper()
    private var mList: ArrayList<NotificationsListResponse.Data.Datum>

    init {
        mViewBinderHelper.setOpenOnlyOne(true)
        mList = ArrayList()
    }

    internal fun setList(mList: ArrayList<NotificationsListResponse.Data.Datum>?) {
        if (mList != null) {
            this.mList = mList
//            Collections.reverse(mList)
//            mList.reverse()
        }
    }

    fun saveStates(outState: Bundle) {
        mViewBinderHelper.saveStates(outState)
    }

    fun restoreStates(inState: Bundle) {
        mViewBinderHelper.restoreStates(inState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_notification, parent, false)
            CustomViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_notification_with_label, parent, false)
            CustomLabelViewHolder(itemView)
        }
    }

    override fun getItemViewType(position: Int): Int {

        val notificationType = mList[position].notificationType
        return if (notificationType == "Today" || notificationType == "Yesterday"
            || notificationType == "Previous") {
            1
        } else {
            0
        }

        /*   return if (position == 0) {
               1
           } else {
               val date1 = Utils.getDateFromServerTime(mList[(position - 1)].dueOn ?: "")
               val date2 = Utils.getDateFromServerTime(mList[position].dueOn ?: "")
               if (date1 == date2) {
                   0
               } else {
                   1
               }
           }*/

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) {
            setViewHolder(holder, position)
        } else if (holder is CustomLabelViewHolder) {
            setViewHolder(holder, position)
        }
    }

    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {

        val hasAction = (mList[position].hasActions ?: "")
//        if (hasAction == "1") {
        mViewBinderHelper.closeLayout(position.toString())
//        }

        mViewBinderHelper.bind(mViewHolder.mSwipeRevealLayout, position.toString())

        val color = if (hasAction == "1") {
            ContextCompat.getColor(mActivity, R.color.colorRippleOnSwipe)
        } else {
            ContextCompat.getColor(mActivity, R.color.colorWhite)
        }
        mViewHolder.mSwipeRevealLayout.setBackgroundColor(color)
        mViewHolder.mLayoutParent.setCardBackgroundColor(color)

        if (hasAction == "1") {
            mViewHolder.mSwipeRevealLayout.setLockDrag(false)
        } else {
            mViewHolder.mSwipeRevealLayout.setLockDrag(true)
        }

        mViewHolder.tvMessage.text = (mList[position].message ?: "")
        mViewHolder.tvDate.text = Utils.getDifference(mList[position].dueOn ?: "")

        mViewHolder.mLayoutAccept.setOnClickListener {
            mListener.onItemClickItem("accept", position, mList[position])
        }

        mViewHolder.mLayoutDecline.setOnClickListener {
            mListener.onItemClickItem("decline", position, mList[position])
        }

        /*mViewHolder.mSwipeRevealLayout.setSwipeListener(object : SwipeRevealLayout.SwipeListener {

            override fun onOpened(view: SwipeRevealLayout?) {
                AppLogger.e(" onOpened ")
            }

            override fun onClosed(view: SwipeRevealLayout?) {
                AppLogger.e(" onClosed ")
            }

            @SuppressLint("ResourceType")
            override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
                AppLogger.e(" onSlide ")
            }

        })*/
    }

    private fun setViewHolder(mViewHolder: CustomLabelViewHolder, position: Int) {
        val hasAction = (mList[position].hasActions ?: "")
//        if (hasAction == "1") {
        mViewBinderHelper.closeLayout(position.toString())
//        }

        mViewBinderHelper.bind(mViewHolder.mSwipeRevealLayout, position.toString())

        val color = if (hasAction == "1") {
            ContextCompat.getColor(mActivity, R.color.colorRippleOnSwipe)
        } else {
            ContextCompat.getColor(mActivity, R.color.colorWhite)
        }
        mViewHolder.mSwipeRevealLayout.setBackgroundColor(color)
        mViewHolder.mLayoutParent.setCardBackgroundColor(color)

        if (hasAction == "1") {
            mViewHolder.mSwipeRevealLayout.setLockDrag(false)
        } else {
            mViewHolder.mSwipeRevealLayout.setLockDrag(true)
        }

        mViewHolder.tvMessage.text = (mList[position].message ?: "")
        mViewHolder.tvDate.text = Utils.getDifference(mList[position].dueOn ?: "")

//        mViewHolder.tvTitleDate.text = Utils.getDifferenceDay(mList[position].dueOn ?: "")
        mViewHolder.tvTitleDate.text = mList[position].notificationType

        /*mViewHolder.mSwipeRevealLayout.setSwipeListener(object : SwipeRevealLayout.SwipeListener {

            override fun onOpened(view: SwipeRevealLayout?) {
                AppLogger.e(" onOpened ")
            }

            override fun onClosed(view: SwipeRevealLayout?) {
                AppLogger.e(" onClosed ")
            }

            override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
                AppLogger.e(" onSlide ")
            }

        })*/


        mViewHolder.mLayoutAccept.setOnClickListener {
            mListener.onItemClickItem("accept", position, mList[position])
        }

        mViewHolder.mLayoutDecline.setOnClickListener {
            mListener.onItemClickItem("decline", position, mList[position])
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    internal class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mSwipeRevealLayout: SwipeRevealLayout = view.findViewById(R.id.mSwipeRevealLayout)
        val mLayoutParent: MaterialCardView = view.findViewById(R.id.mMaterialCardView)

        val tvMessage: MaterialTextView = view.findViewById(R.id.tvMessage)
        val tvDate: MaterialTextView = view.findViewById(R.id.tvDate)
//        val ivProfile: CircleImageView = view.findViewById(R.id.ivProfile)

        val mLayoutDecline: LinearLayout = view.findViewById(R.id.mLayoutDecline)
        val mLayoutAccept: LinearLayout = view.findViewById(R.id.mLayoutAccept)

    }

    internal class CustomLabelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mSwipeRevealLayout: SwipeRevealLayout = view.findViewById(R.id.mSwipeRevealLayout)
        val mLayoutParent: MaterialCardView = view.findViewById(R.id.mMaterialCardView)

        val tvTitleDate: MaterialTextView = view.findViewById(R.id.tvTitleDate)
        val tvMessage: MaterialTextView = view.findViewById(R.id.tvMessage)
        val tvDate: MaterialTextView = view.findViewById(R.id.tvDate)
//        val ivProfile: CircleImageView = view.findViewById(R.id.ivProfile)

        val mLayoutDecline: LinearLayout = view.findViewById(R.id.mLayoutDecline)
        val mLayoutAccept: LinearLayout = view.findViewById(R.id.mLayoutAccept)

    }

}
