package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.Day

class ComputedDayDiffOf(private val old: Day, private val new: Day) : ComputedDayDiff {
    override fun value(): Day {
        return new.copy(
            lessons = ComputedLessonsDiffOf(old = old.lessons, new = new.lessons).value()
        )
    }
}
