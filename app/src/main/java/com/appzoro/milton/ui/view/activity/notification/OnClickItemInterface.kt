package com.appzoro.milton.ui.view.activity.notification

import com.appzoro.milton.model.NotificationsListResponse

interface OnClickItemInterface {

    fun onItemClickItem(type: String, pos: Int, item: NotificationsListResponse.Data.Datum)

}