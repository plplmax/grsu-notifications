package com.github.plplmax.notifications.data.notification.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Date

class NotificationPagingSource(
    private val notifications: ScheduleNotifications,
    private val pagingLoad: PagingLoad<Date, ShortScheduleDiffNotification>,
    coroutineScope: CoroutineScope
) : PagingSource<Date, ShortScheduleDiffNotification>() {
    init {
        coroutineScope.launch {
            notifications.changesetFlow()
                .take(1)
                .onEach { invalidate() }
                .catch { invalidate() }
                .collect()
        }
    }

    override fun getRefreshKey(state: PagingState<Date, ShortScheduleDiffNotification>): Date? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.let { Date.from(it.created.toInstant()) }
        }
    }

    override suspend fun load(params: LoadParams<Date>): LoadResult<Date, ShortScheduleDiffNotification> {
        return when (params) {
            is LoadParams.Refresh -> {
                params.key?.let { key ->
                    pagingLoad.refresh(key = key, loadSize = params.loadSize.toLong())
                } ?: pagingLoad.initial(loadSize = params.loadSize.toLong())
            }

            is LoadParams.Append -> pagingLoad.append(
                key = params.key,
                loadSize = params.loadSize.toLong()
            )

            is LoadParams.Prepend -> pagingLoad.prepend(
                key = params.key,
                loadSize = params.loadSize.toLong()
            )
        }
    }
}
