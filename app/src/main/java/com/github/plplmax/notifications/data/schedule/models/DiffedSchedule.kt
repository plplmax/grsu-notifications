package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class DiffedSchedule(val id: String = "", val days: List<Day> = listOf())

fun DiffedSchedule.toRealm(): DiffedScheduleRealm {
    return DiffedScheduleRealm().apply {
        id = ObjectId(this@toRealm.id)
        days = this@toRealm.days.map(Day::toRealm).toTypedArray().let { RealmList(*it) }
    }
}