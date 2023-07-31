package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class DiffedScheduleRealm : RealmObject() {
    var days: RealmList<DayRealm> = RealmList()
}

fun DiffedScheduleRealm.toData(): DiffedSchedule {
    return DiffedSchedule(days = days.map(DayRealm::toData))
}
