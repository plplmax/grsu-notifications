package com.github.plplmax.grsunotifications.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.plplmax.grsunotifications.R

class ScheduleNotificationChannel(
    private val context: Context,
    private val centre: NotificationCentre
) : NotificationChannel {
    override val builder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent())
            .setAutoCancel(true)

    private val channel: NotificationChannelCompat
        get() = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(context.getString(R.string.channel_name))
            .setDescription(context.getString(R.string.channel_description))
            .setVibrationEnabled(true)
            .setLightsEnabled(true)
            .build()

    override fun create() {
        this.centre.createChannel(channel)
    }

    override fun delete() {
        this.centre.deleteChannel(CHANNEL_ID)
    }

    override fun send(notification: Notification) {
        this.centre.send(NOTIFICATION_ID, notification)
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                COMPONENT_PACKAGE,
                COMPONENT_CLASS
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            pendingFlags
        )
    }

    companion object {
        private const val CHANNEL_ID = "1"
        private const val NOTIFICATION_ID = 1
        private const val COMPONENT_PACKAGE = "com.grsu.schedule"
        private const val COMPONENT_CLASS = "com.grsu.schedule.activities.HomeActivity"
    }
}
