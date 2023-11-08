package com.github.plplmax.notifications.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.data.notification.paging.NotificationPagingSource
import com.github.plplmax.notifications.data.notification.paging.NotificationsPagingLoad
import com.github.plplmax.notifications.data.notification.paging.SafeNotificationsPagingLoad
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notifications: ScheduleNotifications,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    val paging: Flow<PagingData<ShortScheduleDiffNotification>> =
        Pager(config = PagingConfig(pageSize = 20, enablePlaceholders = true)) {
            NotificationPagingSource(
                notifications = notifications,
                pagingLoad = SafeNotificationsPagingLoad(
                    NotificationsPagingLoad(notifications)
                ),
                viewModelScope
            )
        }.flow.cachedIn(viewModelScope)

    fun deleteNotificationAsync(id: String): Deferred<Boolean> {
        return viewModelScope.async(ioDispatcher) {
            try {
                notifications.deleteById(id)
                true
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                false
            }
        }
    }

    fun readNotification(id: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                notifications.read(id)
            } catch (_: Exception) {
                // @todo think about handling exceptions
            }
        }
    }
}
