package com.github.plplmax.notifications.data.diffedSchedule

import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule

interface DiffedScheduleRepository {
    suspend fun save(schedule: DiffedSchedule): String
    suspend fun schedules(): List<DiffedSchedule>
    suspend fun scheduleById(id: String): List<DiffedSchedule>
    suspend fun deleteById(id: String)
}
