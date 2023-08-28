package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.Lesson

interface ComputedLessonDiff {
    fun value(): List<Lesson>
}
