package com.github.plplmax.notifications.data.notification

import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.ui.notification.Comparison
import io.realm.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date

class LoggedScheduleNotifications(
    private val origin: ScheduleNotifications,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleNotifications {
    override suspend fun save(notification: ScheduleDiffNotification) {
        withContext(dispatcher) {
            try {
                origin.save(notification)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun deleteById(id: String) {
        withContext(dispatcher) {
            try {
                origin.deleteById(id)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun notificationById(id: String): List<ScheduleDiffNotification> {
        return withContext(dispatcher) {
            try {
                origin.notificationById(id)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun notifications(): List<ShortScheduleDiffNotification> {
        return withContext(dispatcher) {
            try {
                origin.notifications()
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun notifications(
        date: Date,
        limit: Long,
        comparison: Comparison,
        sort: Sort
    ): List<ShortScheduleDiffNotification> {
        return withContext(dispatcher) {
            try {
                origin.notifications(date, limit, comparison, sort)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override suspend fun read(id: String) {
        withContext(dispatcher) {
            try {
                origin.read(id)
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                Timber.e(e)
                throw e
            }
        }
    }

    override fun changesetFlow(): Flow<Boolean> {
        return origin.changesetFlow()
            .catch {
                Timber.e(it)
                throw it
            }
    }
}
