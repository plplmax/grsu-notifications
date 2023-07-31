package com.github.plplmax.notifications.notification

import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule
import com.github.plplmax.notifications.data.schedule.models.toRealm
import org.bson.types.ObjectId
import java.time.ZonedDateTime
import java.util.Date

data class ScheduleDiffNotification(
    val id: String = "",
    val read: Boolean = false,
    val created: ZonedDateTime = ZonedDateTime.now(),
    val diff: DiffedSchedule = DiffedSchedule()
)

fun ScheduleDiffNotification.toRealm(): ScheduleDiffNotificationRealm {
    return ScheduleDiffNotificationRealm().apply {
        id = if (this@toRealm.id.isEmpty()) ObjectId() else ObjectId(this@toRealm.id)
        read = this@toRealm.read
        created = Date.from(this@toRealm.created.toInstant())
        diff = this@toRealm.diff.toRealm()
    }
}
