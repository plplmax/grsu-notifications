package com.github.plplmax.notifications.time

import java.time.LocalTime

class NightTimeOf(
    private val startInclusive: LocalTime,
    private val endExclusive: LocalTime
) : NightTime {
    override fun isNight(time: LocalTime): Boolean {
        return time >= startInclusive && time < endExclusive
    }
}
