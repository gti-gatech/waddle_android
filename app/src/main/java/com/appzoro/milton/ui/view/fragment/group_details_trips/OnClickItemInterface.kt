package com.appzoro.milton.ui.view.fragment.group_details_trips

import android.view.View
import com.appzoro.milton.model.DatumModel

interface OnClickItemInterface {

    fun onItemClickItem(view: View, pos: Int, item: DatumModel,type:Int)

}