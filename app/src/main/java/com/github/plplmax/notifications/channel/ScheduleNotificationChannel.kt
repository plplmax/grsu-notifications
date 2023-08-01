package com.github.plplmax.notifications.channel

interface ScheduleNotificationChannel : NotificationChannel {
    fun cancelFailedNotifications()
}
