package com.github.plplmax.notifications.data.schedule.local

import com.github.plplmax.notifications.data.schedule.models.DiffedScheduleRealm
import com.github.plplmax.notifications.data.schedule.models.ScheduleRealm

interface ScheduleLocalDataSource {
    suspend fun insert(schedule: ScheduleRealm)
    suspend fun insert(schedule: DiffedScheduleRealm)
    suspend fun schedule(): List<ScheduleRealm>
    suspend fun diffedScheduleById(id: String): DiffedScheduleRealm
    suspend fun deleteSchedule()
    suspend fun deleteDiffedScheduleById(id: String)
}
