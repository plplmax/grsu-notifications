package com.github.plplmax.notifications.data.schedule.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass(embedded = true)
open class TeacherRealm : RealmObject() {
    var fullname: String = ""
}

fun TeacherRealm.toData(): Teacher {
    return Teacher(fullname = fullname)
}
