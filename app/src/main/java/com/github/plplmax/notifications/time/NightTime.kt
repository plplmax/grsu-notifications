package com.github.plplmax.notifications.time

import java.time.LocalTime

interface NightTime {
    fun isNight(time: LocalTime): Boolean
}
