package com.github.plplmax.notifications.data.schedule.models

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val fullname: String,
    val post: String,
)

fun Teacher.toRealm(): TeacherRealm {
    return TeacherRealm().apply {
        fullname = this@toRealm.fullname
        post = this@toRealm.post
    }
}
