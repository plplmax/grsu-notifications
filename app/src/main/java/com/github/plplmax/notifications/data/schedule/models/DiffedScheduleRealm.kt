package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class DiffedScheduleRealm : RealmObject() {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var days: RealmList<DayRealm> = RealmList()
}

fun DiffedScheduleRealm.toData(): DiffedSchedule {
    return DiffedSchedule(id = id.toString(), days = days.map(DayRealm::toData))
}
