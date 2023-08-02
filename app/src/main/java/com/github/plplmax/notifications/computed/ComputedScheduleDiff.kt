package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.ScheduleDiff

interface ComputedScheduleDiff {
    fun value(): ScheduleDiff
}
