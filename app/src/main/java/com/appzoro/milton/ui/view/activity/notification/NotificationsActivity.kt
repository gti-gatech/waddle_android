package com.appzoro.milton.ui.view.activity.notification

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.databinding.ActivityNotificationsBinding
import com.appzoro.milton.model.DefaultResponse
import com.appzoro.milton.model.DialogMessage
import com.appzoro.milton.model.NotificationsListResponse
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.login.LoginActivity
import com.appzoro.milton.ui.view.activity.onboarding.OnBoardingActivity
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.layout_header.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NotificationsActivity : BaseActivity(), View.OnClickListener, OnClickItemInterface {
    private var mAdapter: NotificationAdapter? = null
    private var mItemSelected: NotificationsListResponse.Data.Datum? = null
    private var comeFor: String = ""
    private var mPreferences: PreferenceManager? = null

    private lateinit var dataBinding: ActivityNotificationsBinding
    private lateinit var mViewModel: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)
        mViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        dataBinding.notificationsViewModel = mViewModel
        dataBinding.lifecycleOwner = this
        mPreferences = PreferenceManager(this)

        if (intent.hasExtra("comeForm")) {
            comeFor = intent.getStringExtra("comeFor") ?: ""
            AppLogger.e("Notification Activity comeFor $comeFor")
        }

        if ((mPreferences?.getString(Constant.isLogin) ?: "") == "1") {
            manageCallbacks()
            initializeViews()
            mViewModel.callApiForGetNotifications()
        } else {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .putExtra("comeFrom", "logout")
            )
            finish()
        }

    }

    private fun initializeViews() {
        textViewTitle.text = getString(R.string.notifications)
        imageViewBack.setOnClickListener(this)
        mAdapter = NotificationAdapter(this, this)
        mRecyclerToday?.adapter = mAdapter
        mRecyclerToday.addItemDecoration(SpacesItemDecoration(15))
    }

    private fun manageCallbacks() {

        mViewModel.callback.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                if (it is String) Utils.alertDialog(
                    this,
                    it
                ) else if (it is DialogMessage) Utils.alertDialog(
                    this,
                    it.message
                ) else if (it is Boolean) {
                    if (it) showLoading() else hideLoading()

                } else if (it is DefaultResponse) {
                    Utils.alertDialog(this, (it.message))
                    mItemSelected?.hasActions = "0"
                    mAdapter?.notifyDataSetChanged()
                } else if (it is NotificationsListResponse) manageNotificationData(it) else AppLogger.e(
                    " else "
                )
            }
        })

    }

    private fun manageNotificationData(mResponse: NotificationsListResponse) {
        if (mResponse.data != null) {
            if (mResponse.data?.today == null) {
                mResponse.data?.today = ArrayList()
            }

            if (mResponse.data?.today != null) {
                if ((mResponse.data?.today?.size
                        ?: 0) > 0
                ) mResponse.data?.today?.get(0)?.notificationType = "Today"
            }
            if (mResponse.data?.yesterday != null) {
                if ((mResponse.data?.yesterday?.size ?: 0) > 0) {
                    mResponse.data?.yesterday?.get(0)?.notificationType = "Yesterday"
                    mResponse.data?.today?.addAll(mResponse.data?.yesterday!!)
                }
            }
            if (mResponse.data?.previous != null) {
                if ((mResponse.data?.previous?.size ?: 0) > 0) {
                    mResponse.data?.previous?.get(0)?.notificationType = "Previous"
                    mResponse.data?.today?.addAll(mResponse.data?.previous!!)
                }
            }
            mAdapter?.setList(mResponse.data?.today)
            mAdapter?.notifyDataSetChanged()
            if (mResponse.data?.today?.size ?: 0 > 0) tvNoData.visibility =
                View.GONE else tvNoData.visibility = View.VISIBLE
        } else tvNoData.visibility = View.VISIBLE

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                onBackPressed()
            }
            R.id.buttonAgree -> {
                startActivity(Intent(this, OnBoardingActivity::class.java))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mAdapter?.saveStates(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mAdapter?.restoreStates(savedInstanceState)
    }

    override fun onItemClickItem(
        type: String,
        pos: Int,
        item: NotificationsListResponse.Data.Datum
    ) {
        this.mItemSelected = item
        AppLogger.e("onItemClickItem type $type")
        if (type == "accept") mViewModel.callApiForTripsStart(
            item.id ?: ""
        ) else mViewModel.callApiForNotificationReadMark(item.id ?: "")
    }

    override fun onBackPressed() {
        if (comeFor == "notification") {
            startActivity(
                Intent(this, DashBoardActivity::class.java)
                    .putExtra("comeFrom", "")
            )
            finish()
        } else super.onBackPressed()
    }

}