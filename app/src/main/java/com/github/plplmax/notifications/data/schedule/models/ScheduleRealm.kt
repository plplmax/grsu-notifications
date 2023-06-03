package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import io.realm.RealmObject
import org.bson.types.ObjectId

open class ScheduleRealm : RealmObject() {
    var id: ObjectId = ObjectId()
    var days: RealmList<DayRealm> = RealmList()
}

fun ScheduleRealm.toData(): Schedule {
    return Schedule(days.map(DayRealm::toData))
}
