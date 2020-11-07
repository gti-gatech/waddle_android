package com.appzoro.milton.utility

import android.content.Context
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.LoginResponse

object PreferencesData {

    fun setLoginPreference(mContext: Context, mData: LoginResponse.Data?) {

        val mPreferences = PreferenceManager(mContext)

        if ((mData?.parentData?.parentId ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.parentId,
                mData?.parentData?.parentId ?: ""
            )
        }
        if ((mData?.parentData?.fullName ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.fullName,
                mData?.parentData?.fullName ?: ""
            )
        }
        if ((mData?.parentData?.email ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.email,
                mData?.parentData?.email ?: ""
            )
        }
        if ((mData?.parentData?.contact ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.contact,
                mData?.parentData?.contact ?: ""
            )
        }
        if ((mData?.parentData?.address ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.address,
                mData?.parentData?.address ?: ""
            )
        }
        if ((mData?.parentData?.createdOn ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.createdOn,
                mData?.parentData?.createdOn ?: ""
            )
        }
        if ((mData?.parentData?.isFirstTime ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.isFirstTime,
                mData?.parentData?.isFirstTime ?: ""
            )
        }
        if ((mData?.parentData?.totalTrips ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.totalTrips,
                mData?.parentData?.totalTrips ?: ""
            )
        }
        if ((mData?.parentData?.totalStudents ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.totalStudents,
                mData?.parentData?.totalStudents ?: ""
            )
        }
        if ((mData?.parentData?.image ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.image,
                mData?.parentData?.image ?: ""
            )
        }
        if ((mData?.parentData?.stopId ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.stopId,
                mData?.parentData?.stopId ?: ""
            )
        }
        if ((mData?.parentData?.stopName ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.stopName,
                mData?.parentData?.stopName ?: ""
            )
        }




        if ((mData?.auth?.type ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.authType,
                mData?.auth?.type ?: ""
            )
        }
        if ((mData?.auth?.authToken ?: "").isNotEmpty()) {
            mPreferences.setString(
                Constant.authToken,
                mData?.auth?.authToken ?: ""
            )
        }


    }
}