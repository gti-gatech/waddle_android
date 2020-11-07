package com.appzoro.milton.ui.view.fragment.group_details_trips

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.DatumModel
import com.appzoro.milton.ui.view.activity.trackmap.TrackMapActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.util.*

class TripsGroupDetailsAdapter(
    private val mContext: Activity,
    private val mPreference: PreferenceManager,
    private val mListener: OnClickItemInterface
) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<DatumModel>
    var isButtonTripVisible: Boolean = false

    init {
        mList = ArrayList()
    }

    internal fun setList(mList: ArrayList<DatumModel>?) {
        AppLogger.e("mList Size ${mList?.size ?: 0}")
        if (mList != null) {
            this.mList = mList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_trips_group_item_list, parent, false)
        return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CustomViewHolder) {
            setViewHolder(holder, position)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewHolder(mViewHolder: CustomViewHolder, position: Int) {
        val date = "Date: " + Utils.getDateFromTripDate(mList[position].getTripDate())
        mViewHolder.tvGroupDate.text = date
        mViewHolder.tvGroupName.text = mList[position].getGroupName()

        val pickUpStopName: String =
            if (mList[position].getPickupStopNamee().isNullOrEmpty()) Constant.emptyString
            else
                mList[position].getPickupStopNamee()!!
        mViewHolder.tvPickupStop.text = "Pickup Stop: $pickUpStopName"
        val superVisorName: String =
            if (mList[position].getSupervisorName() == null)
                "Supervisor: ${Constant.emptyString}"
            else
                "Supervisor: ${mList[position].getSupervisorName()}"
        mViewHolder.tvSupervisorName.text = superVisorName

        when (mList[position].getStatus()) {
            "TRIP_NOT_STARTED" -> {
                if ((mPreference.getString(Constant.parentId) == mList[position].getSupervisorId())
                    && mList[position].getStartTripFlag() == 1
                ) {
                    mViewHolder.btnStartTrip.visibility = View.VISIBLE
                    mViewHolder.btnComplete.visibility = View.GONE
                    isButtonTripVisible = true
                } else {
                    mViewHolder.btnComplete.visibility = View.GONE
                    mViewHolder.btnStartTrip.visibility = View.GONE
                    isButtonTripVisible = false
                }
            }
            "TRIP_STARTED" -> {
                if ((mPreference.getString(Constant.parentId) == mList[position].getSupervisorId())
                ) {
                    mViewHolder.btnStartTrip.visibility = View.GONE
                    mViewHolder.btnComplete.visibility = View.VISIBLE
                } else {
                    mViewHolder.btnStartTrip.visibility = View.GONE
                    mViewHolder.btnComplete.visibility = View.GONE
                }
            }
            else -> {
                isButtonTripVisible = false
                mViewHolder.btnComplete.visibility = View.GONE
                mViewHolder.btnStartTrip.visibility = View.GONE
            }
        }

        if (mList[position].getSupervisorStar() == 1) mViewHolder.tvSupervisorStar.visibility = View.VISIBLE else mViewHolder.tvSupervisorStar.visibility = View.GONE

        //  supervisorStar   0   ,1
        //  type   0: 3Dots click   ,1: start, 2:end trip
        mViewHolder.ivEditSchedule.setOnClickListener {
            mListener.onItemClickItem(
                mViewHolder.ivEditSchedule,
                mViewHolder.adapterPosition,
                mList[mViewHolder.adapterPosition], 0
            )
        }

        mViewHolder.btnStartTrip.setOnClickListener {
            mListener.onItemClickItem(
                mViewHolder.btnStartTrip,
                mViewHolder.adapterPosition,
                mList[mViewHolder.adapterPosition], 1
            )

        }
        mViewHolder.btnComplete.setOnClickListener {
            mListener.onItemClickItem(
                mViewHolder.btnComplete,
                mViewHolder.adapterPosition,
                mList[mViewHolder.adapterPosition], 2
            )

        }
        mViewHolder.itemView.setOnClickListener {

            mContext.startActivity(
                Intent(mContext, TrackMapActivity::class.java).putExtra(
                    "tripId",
                    mList[position].getTripId()
                ).putExtra(
                    "groupId",
                    mList[position].getGroupId()
                ).putExtra("isSupervisor", mList[position].getSupervisorStar())
            )
        }
    }

    internal class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvSupervisorStar: MaterialTextView = view.findViewById(R.id.tvSupervisorStar)
        val tvGroupDate: MaterialTextView = view.findViewById(R.id.tvGroupDate)
        val tvGroupName: MaterialTextView = view.findViewById(R.id.tvGroupName)
        val tvPickupStop: MaterialTextView = view.findViewById(R.id.tvPickupStop)
        val tvSupervisorName: MaterialTextView = view.findViewById(R.id.tvSupervisorName)
        val ivEditSchedule: AppCompatImageView = view.findViewById(R.id.ivEditSchedule)
        val btnComplete: MaterialButton = view.findViewById(R.id.btnComplete)
        val btnStartTrip: MaterialButton = view.findViewById(R.id.btnStartTrip)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
