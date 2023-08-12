package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.Lesson

interface ComputedLessonsDiff {
    fun value(): List<Lesson>
}
