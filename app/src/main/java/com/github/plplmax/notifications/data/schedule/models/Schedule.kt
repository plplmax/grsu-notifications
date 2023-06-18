package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import kotlinx.serialization.Serializable

@Serializable
data class Schedule(val days: List<Day>)

fun Schedule.toRealm(): ScheduleRealm {
    return ScheduleRealm().apply {
        days = this@toRealm.days.map(Day::toRealm).toTypedArray().let { RealmList(*it) }
    }
}
