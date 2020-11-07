package com.appzoro.milton.network

import android.content.Context
import com.appzoro.milton.R
import com.appzoro.milton.model.ErrorResponse
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Utils
import com.google.gson.Gson
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UtilThrowable {

    companion object {

        fun mCheckThrowable(throwable: Throwable, mContext: Context): ErrorFailure {

            val title = mContext.getString(R.string.error)
            val messageInternet = mContext.getString(R.string.no_internet_connection)

            AppLogger.e("isOnline >>> ${Utils.isOnline(mContext)}")

            return if (Utils.isOnline(mContext)) {
                when (throwable) {
                    is ConnectException -> ErrorFailure(102, title, messageInternet)
                    is SocketTimeoutException -> ErrorFailure(103, title, mContext.getString(R.string.try_again_after))
                    is UnknownHostException -> ErrorFailure(104, title, mContext.getString(R.string.server_not_available))
                    is java.lang.IllegalStateException -> ErrorFailure(105, title, throwable.message ?: "")
                    is com.google.gson.JsonSyntaxException -> ErrorFailure(106, title, throwable.message ?: "")
                    is HttpException -> {
                        val error = Gson().fromJson(
                            throwable.response()?.errorBody()?.string(),
                            ErrorResponse::class.java
                        )
                        AppLogger.e("error  $error")
                        ErrorFailure(107, error.type, error.message)
                    }
                    else -> ErrorFailure(
                        101, title, messageInternet
                    )
                }
            } else {
                ErrorFailure(100, title, messageInternet)
            }
        }

    }
}

class ErrorFailure(code: Int, title: String, message: String) {
    var mTitle: String = title
    var mMessage: String = message
    var mCode: Int = code
}


