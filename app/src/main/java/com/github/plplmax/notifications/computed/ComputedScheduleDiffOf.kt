package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.schedule.models.ScheduleDiff

class ComputedScheduleDiffOf(
    private val old: Schedule,
    private val new: Schedule
) : ComputedScheduleDiff {
    override fun value(): ScheduleDiff {
        return ScheduleDiff(
            days = ComputedDaysDiffOf(old = old.days, new = new.days).value()
        )
    }
}
