package com.github.plplmax.notifications.notification

import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule
import com.github.plplmax.notifications.data.schedule.models.DiffedScheduleRealm
import com.github.plplmax.notifications.data.schedule.models.toData
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

open class ScheduleDiffNotificationRealm : RealmObject() {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var read: Boolean = false
    var created: Date = Date()
    var diff: DiffedScheduleRealm? = DiffedScheduleRealm()
}

fun ScheduleDiffNotificationRealm.toData(): ScheduleDiffNotification {
    return ScheduleDiffNotification(
        id = id.toString(),
        read = read,
        created = ZonedDateTime.ofInstant(created.toInstant(), ZoneId.systemDefault()),
        diff = diff?.toData() ?: DiffedSchedule()
    )
}

fun ScheduleDiffNotificationRealm.toShortData(): ShortScheduleDiffNotification {
    return ShortScheduleDiffNotification(
        id = id.toString(),
        read = read,
        created = ZonedDateTime.ofInstant(created.toInstant(), ZoneId.systemDefault())
    )
}
