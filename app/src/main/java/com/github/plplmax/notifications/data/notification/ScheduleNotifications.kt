package com.github.plplmax.notifications.data.notification

import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification

interface ScheduleNotifications {
    suspend fun save(notification: ScheduleDiffNotification)
    suspend fun deleteById(id: String)
    suspend fun notificationById(id: String): List<ScheduleDiffNotification>
    suspend fun notifications(): List<ShortScheduleDiffNotification>
    suspend fun read(id: String)
}
