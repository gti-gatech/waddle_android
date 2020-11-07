package com.appzoro.milton.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.appzoro.milton.utility.Utils.Companion.showLoadingDialog

abstract class BaseFragment: Fragment(),FactoryInterface {
    private var mActivity: BaseActivity? = null
    private var mProgressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            mActivity = context
            context.onFragmentAttached()
        }
    }


    override fun showLoading() {
        hideLoading()
        mProgressDialog = showLoadingDialog(this.context!!)
    }

    override fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
            mProgressDialog!!.cancel()
        }
    }

    override fun openActivityOnTokenExpire() {
        if (mActivity != null) {
            mActivity!!.openActivityOnTokenExpire()
        }
    }

    override fun onError(resId: Int) {
        if (mActivity != null) {
            mActivity!!.onError(resId)
        }
    }

    override fun onError(message: String?) {
        if (mActivity != null) {
            mActivity!!.onError(message)
        }
    }

    override fun onSuccess() {
        hideLoading()
    }

    override fun showMessage(message: String?) {
        if (mActivity != null) {
            mActivity!!.showMessage(message)
        }
    }

    override fun showMessage(resId: Int) {
        if (mActivity != null) {
            mActivity!!.showMessage(resId)
        }
    }

    override fun isNetworkConnected(): Boolean {
        return mActivity?.isNetworkConnected() ?: false
    }

    override fun hideKeyboard() {
        if (mActivity != null) {
            mActivity!!.hideKeyboard()
        }
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }
    protected abstract fun setUp(view: View)
    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }

    override fun onDestroyView() {
        hideLoading()
        super.onDestroyView()
    }

    interface Callback {
        fun onFragmentAttached()
        fun onFragmentDetached(tag: String?)
    }
}