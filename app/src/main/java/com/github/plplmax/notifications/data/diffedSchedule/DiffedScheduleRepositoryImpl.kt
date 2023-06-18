package com.github.plplmax.notifications.data.diffedSchedule

import com.github.plplmax.notifications.data.diffedSchedule.local.DiffedScheduleLocalDataSource
import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule
import com.github.plplmax.notifications.data.schedule.models.DiffedScheduleRealm
import com.github.plplmax.notifications.data.schedule.models.toData
import com.github.plplmax.notifications.data.schedule.models.toRealm
import org.bson.types.ObjectId

class DiffedScheduleRepositoryImpl(
    private val local: DiffedScheduleLocalDataSource
) : DiffedScheduleRepository {
    override suspend fun save(schedule: DiffedSchedule): String {
        return local.insert(schedule.toRealm()).toString()
    }

    override suspend fun schedules(): List<DiffedSchedule> {
        return local.schedules().map(DiffedScheduleRealm::toData)
    }

    override suspend fun scheduleById(id: String): List<DiffedSchedule> {
        return local.scheduleById(ObjectId(id)).map(DiffedScheduleRealm::toData)
    }

    override suspend fun deleteById(id: String) {
        local.deleteById(ObjectId(id))
    }
}
