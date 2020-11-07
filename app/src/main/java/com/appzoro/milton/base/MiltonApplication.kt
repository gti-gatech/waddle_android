package com.appzoro.milton.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.appzoro.milton.BuildConfig
import com.appzoro.milton.network.ApiEndPoint
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.FirebaseApp
import me.joshuamarquez.sails.io.SailsSocket
import timber.log.Timber
import java.net.URISyntaxException

class MiltonApplication : Application() {
    private var mSocket: Socket? = null
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        registerActivityLifecycleCallbacks(object :ActivityLifecycleCallbacks{
            override fun onActivityPaused(p0: Activity) {

            }

            override fun onActivityStarted(p0: Activity) {
            }

            override fun onActivityDestroyed(p0: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityStopped(p0: Activity) {
            }

            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                if (activity != null) {
                    val activityName = activity.localClassName
                    Log.e("activity ", "name >" + activity.localClassName + " ")
                    if (Build.VERSION.SDK_INT != 26 ) activity.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            override fun onActivityResumed(p0: Activity) {
            }
        })
        //for socket connection
        //for socket connection
        mSocket = try {
            IO.socket(ApiEndPoint.SOCKET_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

//        mSocket?.on(ApiEndPoint.newMessageListener) {
//
//        }

    }
    fun getSocket(): Socket? {
        return if(mSocket?.connected() == true){
            mSocket
        }else {
            mSocket?.connect()
        }
    }

    companion object {
        operator fun get(context: Context): MiltonApplication? {
            return context.applicationContext as MiltonApplication
        }
    }
}