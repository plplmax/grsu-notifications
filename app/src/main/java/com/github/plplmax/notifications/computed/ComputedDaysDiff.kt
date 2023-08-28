package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.models.Day

interface ComputedDaysDiff {
    fun value(): List<Day>
}
