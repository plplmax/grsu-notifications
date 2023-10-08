package com.github.plplmax.notifications.ui.notification

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import io.realm.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Date

class NotificationPagingSource(
    private val notifications: ScheduleNotifications,
    private val coroutineScope: CoroutineScope
) : PagingSource<Date, ShortScheduleDiffNotification>() {
    init {
        println("init NotificationPagingSource")
        coroutineScope.launch {
            notifications.changesetFlow().take(1).collect {
                println("inside collect: ${currentCoroutineContext()}")
                println(it)
                invalidate()
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Date, ShortScheduleDiffNotification>): Date? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.let { Date.from(it.created.toInstant()) }
        }.also { println("getRefreshKey: $it") }
    }

    override suspend fun load(params: LoadParams<Date>): LoadResult<Date, ShortScheduleDiffNotification> {
        // @todo add try catch block + think about invalidation
//        delay(3000)

        return when (params) {
            is LoadParams.Refresh -> {
                params.key?.let { key ->
                    refreshResult(key = key, loadSize = params.loadSize)
                } ?: initialResult(loadSize = params.loadSize)
            }

            is LoadParams.Append -> appendResult(key = params.key, loadSize = params.loadSize)
            is LoadParams.Prepend -> prependResult(key = params.key, loadSize = params.loadSize)
        }
    }

    private suspend fun initialResult(loadSize: Int): LoadResult<Date, ShortScheduleDiffNotification> {
        val notifs = notifications.notifications(
            date = Date(0),
            limit = loadSize.toLong(),
            comparison = Comparison.GREATER_OR_EQUAL,
            sort = Sort.DESCENDING
        )
        return LoadResult.Page(
            data = notifs,
            prevKey = null,
            nextKey = notifs.lastOrNull()?.let { Date.from(it.created.toInstant()) }
        )
    }

    private suspend fun refreshResult(
        key: Date,
        loadSize: Int
    ): LoadResult<Date, ShortScheduleDiffNotification> {
        val notifs = notifications.notifications(
            date = key,
            limit = loadSize / 2L,
            comparison = Comparison.GREATER_OR_EQUAL,
            sort = Sort.ASCENDING
        ).reversed() + notifications.notifications(
            date = key,
            limit = loadSize / 2L,
            comparison = Comparison.LESS,
            sort = Sort.DESCENDING
        )
        return LoadResult.Page(
            data = notifs,
            prevKey = notifs.firstOrNull()?.let { Date.from(it.created.toInstant()) },
            nextKey = notifs.lastOrNull()?.let { Date.from(it.created.toInstant()) }
        )
    }

    private suspend fun appendResult(
        key: Date,
        loadSize: Int
    ): LoadResult<Date, ShortScheduleDiffNotification> {
        val notifs = notifications.notifications(
            date = key,
            limit = loadSize.toLong(),
            comparison = Comparison.LESS,
            sort = Sort.DESCENDING
        )
        return LoadResult.Page(
            data = notifs,
            prevKey = notifs.firstOrNull()?.let { Date.from(it.created.toInstant()) },
            nextKey = notifs.lastOrNull()?.let { Date.from(it.created.toInstant()) }
        )
    }

    private suspend fun prependResult(
        key: Date,
        loadSize: Int
    ): LoadResult<Date, ShortScheduleDiffNotification> {
        val notifs = notifications.notifications(
            date = key,
            limit = loadSize.toLong(),
            comparison = Comparison.GREATER,
            sort = Sort.ASCENDING
        ).reversed()
        return LoadResult.Page(
            data = notifs,
            prevKey = notifs.firstOrNull()?.let { Date.from(it.created.toInstant()) },
            nextKey = notifs.lastOrNull()?.let { Date.from(it.created.toInstant()) }
        )
    }
}
