package com.github.plplmax.notifications.data.diffedSchedule.local

import com.github.plplmax.notifications.data.schedule.models.DiffedScheduleRealm

interface DiffedScheduleLocalDataSource {
    suspend fun insert(schedule: DiffedScheduleRealm): String
    suspend fun schedules(): List<DiffedScheduleRealm>
    suspend fun scheduleById(id: String): List<DiffedScheduleRealm>
    suspend fun deleteById(id: String)
}
