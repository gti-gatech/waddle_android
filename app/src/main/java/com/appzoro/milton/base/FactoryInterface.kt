package com.appzoro.milton.base

import androidx.annotation.StringRes

interface FactoryInterface {
    fun showLoading()

    fun hideLoading()

    fun openActivityOnTokenExpire()

    fun onError(@StringRes resId: Int)

    fun onError(message: String?)

    fun onSuccess()

    fun showMessage(message: String?)

    fun showMessage(@StringRes resId: Int)

    fun isNetworkConnected(): Boolean

    fun hideKeyboard()
}