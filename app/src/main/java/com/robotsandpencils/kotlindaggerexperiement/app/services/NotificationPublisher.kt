package com.robotsandpencils.kotlindaggerexperiement.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.robotsandpencils.kotlindaggerexperiement.R
import timber.log.Timber
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import com.robotsandpencils.kotlindaggerexperiement.presentation.main.MainActivity


class NotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive: $intent")
        val portalName = intent.getStringExtra("PORTAL_NAME")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = notificationManager.getNotificationChannel("DEFAULT_CHANNEL")
            if (channel == null) {
                channel = NotificationChannel("DEFAULT_CHANNEL", "Expiry Notices", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
        }
        val notification = getNotification(context, "For Portal: $portalName", "DEFAULT_CHANNEL")
        notificationManager.notify(0, notification)
    }

    private fun getNotification(context: Context, content: String, channelId: String): Notification {
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setContentTitle("Flip Expired")
        builder.color = context.getColor(R.color.colorPrimaryDark)
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setAutoCancel(true)
        builder.setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_ONE_SHOT))
        return builder.build()
    }
}
