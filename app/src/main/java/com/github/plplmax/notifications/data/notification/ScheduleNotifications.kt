package com.github.plplmax.notifications.data.notification

import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.ui.notification.Comparison
import io.realm.Sort
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ScheduleNotifications {
    suspend fun save(notification: ScheduleDiffNotification)
    suspend fun deleteById(id: String)
    suspend fun notificationById(id: String): List<ScheduleDiffNotification>
    suspend fun notifications(): List<ShortScheduleDiffNotification>
    suspend fun read(id: String)
    suspend fun notifications(
        date: Date,
        limit: Long,
        comparison: Comparison,
        sort: Sort
    ): List<ShortScheduleDiffNotification>

    fun changesetFlow(): Flow<Boolean>
}
