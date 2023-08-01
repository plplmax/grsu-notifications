package com.github.plplmax.notifications.data.notification

import com.github.plplmax.notifications.notification.ScheduleDiffNotification
import com.github.plplmax.notifications.notification.ShortScheduleDiffNotification

interface ScheduleNotifications {
    suspend fun save(notification: ScheduleDiffNotification)
    suspend fun deleteById(id: String)
    suspend fun notificationById(id: String): List<ScheduleDiffNotification>
    suspend fun notifications(): List<ShortScheduleDiffNotification>
}
