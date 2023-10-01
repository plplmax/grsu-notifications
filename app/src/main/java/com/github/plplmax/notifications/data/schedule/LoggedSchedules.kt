package com.github.plplmax.notifications.data.schedule

import com.github.plplmax.notifications.data.schedule.models.Schedule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate

class LoggedSchedules(
    private val origin: Schedules,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Schedules {
    override suspend fun onWeek(userId: Int, startDate: String, endDate: String): Result<Schedule> {
        return withContext(dispatcher) {
            origin.onWeek(userId, startDate, endDate)
                .onFailure(Timber::e)
        }
    }

    override suspend fun save(schedule: Schedule) {
        try {
            origin.save(schedule)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e)
            throw e
        }
    }

    override suspend fun schedule(): List<Schedule> {
        return try {
            origin.schedule()
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e)
            throw e
        }
    }

    override suspend fun deleteSchedule() {
        try {
            origin.deleteSchedule()
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e)
            throw e
        }
    }

    override suspend fun lastUpdate(): LocalDate {
        return try {
            origin.lastUpdate()
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e)
            throw e
        }
    }

    override suspend fun saveLastUpdate(date: LocalDate) {
        try {
            origin.saveLastUpdate(date)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e)
            throw e
        }
    }

    override suspend fun deleteLastUpdate() {
        try {
            origin.deleteLastUpdate()
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e)
            throw e
        }
    }
}
