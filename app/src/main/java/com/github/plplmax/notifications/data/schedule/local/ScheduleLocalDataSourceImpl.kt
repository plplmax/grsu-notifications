package com.github.plplmax.notifications.data.schedule.local

import com.github.plplmax.notifications.data.database.Database
import com.github.plplmax.notifications.data.schedule.models.ScheduleRealm
import io.realm.kotlin.delete
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleLocalDataSourceImpl(
    private val database: Database,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleLocalDataSource {
    override suspend fun insert(schedule: ScheduleRealm) {
        withContext(dispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction { it.insert(schedule) }
            }
        }
    }

    override suspend fun schedule(): List<ScheduleRealm> {
        return withContext(dispatcher) {
            database.instance().use { realm ->
                realm.where<ScheduleRealm>()
                    .findAll()
                    .let { realm.copyFromRealm(it) }
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
