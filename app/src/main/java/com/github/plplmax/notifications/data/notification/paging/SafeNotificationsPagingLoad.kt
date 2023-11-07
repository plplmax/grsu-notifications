package com.github.plplmax.notifications.data.notification.paging

import androidx.paging.PagingSource
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.util.Date

class SafeNotificationsPagingLoad(
    private val origin: PagingLoad<Date, ShortScheduleDiffNotification>
) : PagingLoad<Date, ShortScheduleDiffNotification> {
    override suspend fun initial(loadSize: Long): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        return try {
            origin.initial(loadSize)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            PagingSource.LoadResult.Error(e)
        }
    }

    override suspend fun prepend(
        key: Date,
        loadSize: Long
    ): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        return try {
            origin.prepend(key, loadSize)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            PagingSource.LoadResult.Error(e)
        }
    }

    override suspend fun append(
        key: Date,
        loadSize: Long
    ): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        return try {
            origin.append(key, loadSize)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            PagingSource.LoadResult.Error(e)
        }
    }

    override suspend fun refresh(
        key: Date,
        loadSize: Long
    ): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        return try {
            origin.refresh(key, loadSize)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            PagingSource.LoadResult.Error(e)
        }
    }
}
