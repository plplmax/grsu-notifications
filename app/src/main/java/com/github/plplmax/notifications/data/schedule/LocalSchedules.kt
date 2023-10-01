package com.github.plplmax.notifications.data.schedule

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
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
import java.time.LocalDate

class LocalSchedules(
    context: Context,
    private val origin: Schedules,
    private val database: Database,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Schedules by origin {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

    override suspend fun lastUpdate(): LocalDate {
        return withContext(dispatcher) {
            prefs.getString(LAST_UPDATE_KEY, null)
                ?.let(LocalDate::parse)
                ?: LocalDate.now()
        }
    }

    override suspend fun saveLastUpdate(date: LocalDate) {
        prefs.edit { putString(LAST_UPDATE_KEY, date.toString()) }
    }

    override suspend fun deleteLastUpdate() {
        prefs.edit { remove(LAST_UPDATE_KEY) }
    }

    companion object {
        private const val PREFS_NAME = "schedules"
        private const val LAST_UPDATE_KEY = "last_update"
    }
}
