package com.github.plplmax.notifications.data.diffedSchedule.local

import com.github.plplmax.notifications.data.database.Database
import com.github.plplmax.notifications.data.schedule.models.DiffedScheduleRealm
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiffedScheduleLocalDataSourceImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DiffedScheduleLocalDataSource {
    override suspend fun insert(schedule: DiffedScheduleRealm) {
        withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction { it.insert(schedule) }
            }
        }
    }

    override suspend fun schedules(): List<DiffedScheduleRealm> {
        return withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.where<DiffedScheduleRealm>()
                    .findAll()
                    .let(realm::copyFromRealm)
            }
        }
    }

    override suspend fun scheduleById(id: String): List<DiffedScheduleRealm> {
        return withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.where<DiffedScheduleRealm>()
                    .equalTo("id", id)
                    .findAll()
                    .let(realm::copyFromRealm)
            }
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(ioDispatcher) {
            database.instance().use { realm ->
                realm.executeTransaction {
                    it.where<DiffedScheduleRealm>()
                        .equalTo("id", id)
                        .findAll()
                        .deleteAllFromRealm()
                }
            }
        }
    }
}
