package com.medrevpatient.mobile.app.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.medrevpatient.mobile.app.R

class NotificationUtils {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        notificationIntent: Intent?,
        id: Int
    ) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var pendingIntent: PendingIntent? = null
        if (notificationIntent != null) {
            notificationIntent.flags =
                (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            val iUniqueId = (System.currentTimeMillis() and 0xfffffff).toInt()
            pendingIntent = PendingIntent.getActivity(
                context,
                iUniqueId,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val channelId = context.packageName + "App"
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round)
        //  val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.)

        notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(bitmap)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)

        notificationBuilder?.color = Color.TRANSPARENT
        if (notificationIntent != null) {
            notificationBuilder?.setContentIntent(pendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            channelComment(channelId, context)
        }
        notificationManager?.notify(id, notificationBuilder?.build())
    }

    private fun channelComment(channelId: String, context: Context) {
        notificationBuilder?.setChannelId(channelId)
        val description = context.getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channelId, "Comment Notifications", importance)
        mChannel.description = description
        mChannel.enableLights(true)
        mChannel.lightColor = Color.parseColor("#3ECAFC")
        mChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(mChannel)
    }
}