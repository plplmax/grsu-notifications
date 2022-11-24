package com.github.plplmax.grsunotifications.notification

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.plplmax.grsunotifications.R

class ScheduleNotification(
    private val context: Context,
    private val title: String = "Уведомления ГрГУ",
    private val text: String = "Расписание было обновлено",
) : Notification {
    private val manager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    override fun send() {
        NotificationCompat.Builder(context, ScheduleNotificationChannel.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent())
            .setAutoCancel(true)
            .build().let { notification ->
                manager.notify(NOTIFICATION_ID, notification)
            }
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
        private const val NOTIFICATION_ID = 1
        private const val COMPONENT_PACKAGE = "com.grsu.schedule"
        private const val COMPONENT_CLASS = "com.grsu.schedule.activities.HomeActivity"
    }
}
