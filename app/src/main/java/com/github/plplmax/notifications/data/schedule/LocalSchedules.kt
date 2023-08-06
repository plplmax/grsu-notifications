package com.github.plplmax.notifications.data.schedule

import com.github.plplmax.notifications.data.database.Database
import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.schedule.models.ScheduleRealm
import com.github.plplmax.notifications.data.schedule.models.toData
import com.github.plplmax.notifications.data.schedule.models.toRealm
import io.realm.kotlin.delete
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalSchedules(
    private val origin: Schedules,
    private val database: Database,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Schedules by origin {
    override suspend fun save(schedule: Schedule) {
        withContext(dispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction { it.insert(schedule.toRealm()) }
            }
        }
    }

    override suspend fun schedule(): List<Schedule> {
        return withContext(dispatcher) {
            database.instance().use { realm ->
                realm.where<ScheduleRealm>()
                    .findAll()
                    .let(realm::copyFromRealm)
                    .map(ScheduleRealm::toData)
            }
        }
    }

    override suspend fun deleteSchedule() {
        withContext(dispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction { it.delete<ScheduleRealm>() }
            }
        }
    }
}
