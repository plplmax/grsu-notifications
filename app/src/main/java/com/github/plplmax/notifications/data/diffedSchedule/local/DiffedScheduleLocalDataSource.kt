package com.github.plplmax.notifications.data.diffedSchedule.local

import com.github.plplmax.notifications.data.schedule.models.DiffedScheduleRealm
import org.bson.types.ObjectId

interface DiffedScheduleLocalDataSource {
    suspend fun insert(schedule: DiffedScheduleRealm): ObjectId
    suspend fun schedules(): List<DiffedScheduleRealm>
    suspend fun scheduleById(id: ObjectId): List<DiffedScheduleRealm>
    suspend fun deleteById(id: ObjectId)
}
