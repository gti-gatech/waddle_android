package com.appzoro.milton.base

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appzoro.milton.utility.Utils
import java.util.concurrent.atomic.AtomicLong

open class BaseActivity: AppCompatActivity() ,FactoryInterface,BaseFragment.Callback{
    private val mActivityId: Long = 0
    companion object{
        private const val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID =
            AtomicLong(0)
    }

    private var mProgressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_ACTIVITY_ID, mActivityId)
    }
    override fun showLoading() {
        hideLoading()
        mProgressDialog = Utils.showLoadingDialog(this)
    }

    override fun hideLoading() {


        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
            mProgressDialog!!.cancel()
        }
    }

    override fun openActivityOnTokenExpire() {
    }

    override fun onError(resId: Int) {
    }

    override fun onError(message: String?) {
    }

    override fun onSuccess() {
    }

    override fun showMessage(message: String?) {
    }

    override fun showMessage(resId: Int) {
    }

    override fun isNetworkConnected(): Boolean {
    return false
    }

    override fun hideKeyboard() {
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String?) {
    }

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }

}