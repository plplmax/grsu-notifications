package com.github.plplmax.notifications.data.schedule.local

import com.github.plplmax.notifications.data.schedule.models.ScheduleRealm

interface ScheduleLocalDataSource {
    suspend fun insert(schedule: ScheduleRealm)
    suspend fun schedule(): List<ScheduleRealm>
    suspend fun deleteSchedule()
}
