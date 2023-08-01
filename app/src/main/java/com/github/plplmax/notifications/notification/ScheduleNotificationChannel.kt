package com.github.plplmax.notifications.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.plplmax.notifications.MainActivity
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.centre.NotificationCentre

interface ScheduleNotificationChannel : NotificationChannel {
    fun cancelFailedNotifications()

    class Base(
        private val context: Context,
        private val centre: NotificationCentre
    ) : ScheduleNotificationChannel {
        override val builder: NotificationCompat.Builder
            get() = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent {})
                .setVibrate(VIBRATION_PATTERN)
                .setAutoCancel(true)

        private val channel: NotificationChannelCompat
            get() = NotificationChannelCompat.Builder(
                CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_HIGH
            )
                .setName(context.getString(R.string.channel_name))
                .setDescription(context.getString(R.string.channel_description))
                .setVibrationPattern(VIBRATION_PATTERN)
                .setVibrationEnabled(true)
                .setLightsEnabled(true)
                .build()

        override fun create() {
            this.centre.createChannel(channel)
        }

        override fun delete() {
            this.centre.deleteChannel(CHANNEL_ID)
        }

        override fun send(id: Int, notification: Notification) {
            this.centre.send(id, notification)
        }

        override fun cancelNotification(id: Int) {
            this.centre.cancelNotification(id)
        }

        override fun cancelFailedNotifications() {
            this.cancelNotification(NotificationType.FAILED.id)
        }

        override fun pendingIntent(configuration: Intent.() -> Unit): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                configuration()
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
            private val VIBRATION_PATTERN = longArrayOf(0, 100, 150, 100)
        }
    }
}
