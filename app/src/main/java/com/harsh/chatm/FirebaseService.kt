package com.harsh.chatm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
private val NOTIFICATION_ID = 101
private val CHANNEL_ID = "my_channel_id"
private val CHANNEL_NAME = "My Channel"
private val CHANNEL_DESCRIPTION = "My Notification Channel"
class FirebaseService : FirebaseMessagingService() {
    val TAG = "SERVICE"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        var notificationData = message.notification
        print("notification title ${notificationData?.title}")
        print("notification body ${notificationData?.body}")
        print("notification data ${message.data.get("Data")}")
        Log.e(TAG, "notification ${message.data}  ${message.data.get("Data")}")
        createNotificationChannel()
        val builder = buildNotification(notificationData?.title.toString(),notificationData?.body.toString(), message.data.get("Data"))
        showNotification(builder)

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "on new token $token")


    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun buildNotification(title: String, text: String, data: String?=null): NotificationCompat.Builder {
        var intent = Intent(this, NewActivity::class.java)
        intent.putExtra("data", data)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP )
        var pendingIntent = PendingIntent.getActivity(this, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.send_message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }
    private fun showNotification(builder: NotificationCompat.Builder) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}