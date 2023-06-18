package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule

interface ComputedScheduleDiff {
    fun value(): DiffedSchedule
}
