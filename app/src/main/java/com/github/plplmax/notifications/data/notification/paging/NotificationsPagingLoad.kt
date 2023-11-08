package com.github.plplmax.notifications.data.notification.paging

import androidx.paging.PagingSource
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.data.Comparison
import io.realm.Sort
import java.util.Date

class NotificationsPagingLoad(
    private val notifications: ScheduleNotifications
) : PagingLoad<Date, ShortScheduleDiffNotification> {
    override suspend fun initial(loadSize: Long): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        val (items, total) = notifications.notifications(
            date = Date(0),
            limit = loadSize,
            comparison = Comparison.GREATER_OR_EQUAL,
            sort = Sort.DESCENDING
        )
        return PagingSource.LoadResult.Page(
            data = items,
            prevKey = null,
            nextKey = items.lastOrNull()?.let { Date.from(it.created.toInstant()) },
            itemsAfter = total.toInt() - items.size
        )
    }

    override suspend fun prepend(
        key: Date,
        loadSize: Long
    ): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        val searchResult = notifications.notifications(
            date = key,
            limit = loadSize,
            comparison = Comparison.GREATER,
            sort = Sort.ASCENDING
        )
        val items = searchResult.items.reversed()
        return PagingSource.LoadResult.Page(
            data = items,
            prevKey = items.firstOrNull()?.let { Date.from(it.created.toInstant()) },
            nextKey = items.lastOrNull()?.let { Date.from(it.created.toInstant()) },
        )
    }

    override suspend fun append(
        key: Date,
        loadSize: Long
    ): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        val (items) = notifications.notifications(
            date = key,
            limit = loadSize,
            comparison = Comparison.LESS,
            sort = Sort.DESCENDING
        )
        return PagingSource.LoadResult.Page(
            data = items,
            prevKey = items.firstOrNull()?.let { Date.from(it.created.toInstant()) },
            nextKey = items.lastOrNull()?.let { Date.from(it.created.toInstant()) },
        )
    }

    override suspend fun refresh(
        key: Date,
        loadSize: Long
    ): PagingSource.LoadResult<Date, ShortScheduleDiffNotification> {
        val before = notifications.notifications(
            date = key,
            limit = loadSize / 2L,
            comparison = Comparison.GREATER_OR_EQUAL,
            sort = Sort.ASCENDING
        )
        val after = notifications.notifications(
            date = key,
            limit = loadSize / 2L,
            comparison = Comparison.LESS,
            sort = Sort.DESCENDING
        )
        val items = before.items.reversed() + after.items
        return PagingSource.LoadResult.Page(
            data = items,
            prevKey = items.firstOrNull()?.let { Date.from(it.created.toInstant()) },
            nextKey = items.lastOrNull()?.let { Date.from(it.created.toInstant()) },
            itemsBefore = before.total.toInt() - before.items.size,
            itemsAfter = after.total.toInt() - after.items.size
        )
    }
}
