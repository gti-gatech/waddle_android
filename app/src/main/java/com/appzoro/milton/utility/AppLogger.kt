package com.appzoro.milton.utility

import com.appzoro.milton.BuildConfig
import timber.log.Timber

object AppLogger {

    fun init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    fun d(s: String, vararg objects: Any) {
        Timber.d(s, *objects)
    }

    fun i(s: String, vararg objects: Any) {
        Timber.i(s, *objects)
    }

    fun v(s: String, vararg objects: Any) {
        Timber.v(s, *objects)
    }

    fun e(s: String, vararg objects: Any) {
        Timber.e(s, *objects)
    }


}
