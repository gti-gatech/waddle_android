package com.appzoro.milton.ui.view.fragment.dashboardschedule

import android.view.View
import com.appzoro.milton.model.GetScheduleResponse

interface OnClickScheduleInterface {

    fun onItemClickItem(view: View, pos: Int, item: GetScheduleResponse.Datum)

}

interface OnThreeDotCLick {
    fun onItemClick(view: View, pos: Int,type:Int)
}