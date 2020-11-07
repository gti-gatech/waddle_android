package com.appzoro.milton.network

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.appzoro.milton.R
import com.appzoro.milton.ui.view.activity.dashboard.DashBoardActivity
import com.appzoro.milton.ui.view.activity.notification.NotificationsActivity
import com.appzoro.milton.utility.AppLogger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class FireBaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        AppLogger.e("onNewToken $p0")
        super.onNewToken(p0)
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        for ((key, value) in remoteMessage.data) {
            AppLogger.e("key:- $key value:- $value")
        }

        if (remoteMessage.data.isNotEmpty()) {
            try {
                val params = remoteMessage.data
                val objectJson = JSONObject(params as Map<*, *>)
                AppLogger.e("JSON_OBJECT $objectJson")

                val type = remoteMessage.data["type"] ?: ""
                val message = remoteMessage.data["message"] ?: ""
//                val payload = remoteMessage.data["payload"] ?: "{}"
                //{
                //	"payload": {
                //		"id": "5551fc35-d2f4-4fbe-af3e-b4531708c626",
                //		"deviceToken": "cHXMPKaATRq86hGFtZRXFi:APA91bEoTWuphkG4bf00NOT4r-zU8qOhdEbGOliZ4Ufk1vp5R4vvROV6lx1igUSND35HpCrK5gjo6EdVxGnrbZKf6XHZwB31P1Xpzo1LETMnSV89paOUSRqJNYbD9ArKq3MJbfJEIe90"
                //	},
                //	"type": "TEST",
                //	"message": "Test message"
                //}

                setPendingIntent(type, message)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun setPendingIntent(type: String, message: String) {



        if(type == "NEW MESSAGE"){
            val intent = Intent(this, DashBoardActivity::class.java)
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("comeFrom", "notification_message")
            redirectNotification(intent,message)

        }else{
            val intent = Intent(this, NotificationsActivity::class.java)
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("comeFrom", "")
            redirectNotification(intent,message)
        }

    }
    fun redirectNotification(intent: Intent, message: String) {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            intent, PendingIntent.FLAG_ONE_SHOT
        )
        sendNotification(message, pendingIntent)
    }

    /*private fun dataForMessaging(payload: String) {

        try {
            val json = JSONObject(payload)
        }catch (e: Exception){
        }
        val mMessageModel = MessageModel()
//        mMessageModel.messageId
    }*/

    private fun sendNotification(message: String?, pendingIntent: PendingIntent) {
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            //  .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setPriority(Notification.PRIORITY_MAX)
//            .setPriority(2)
            .setOnlyAlertOnce(false)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = notificationBuilder.build()
        notificationManager.notify(1 /* ID of notification */, notification)
    }

}