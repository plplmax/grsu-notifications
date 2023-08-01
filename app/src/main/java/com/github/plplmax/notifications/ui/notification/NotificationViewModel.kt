package com.github.plplmax.notifications.ui.notification

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.notification.ShortScheduleDiffNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class NotificationViewModel(
    private val notifications: ScheduleNotifications,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private val cachedNotifications: MutableMap<LocalDate, List<ShortScheduleDiffNotification>> =
        mutableStateMapOf()
    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        uiState = UiState.Loading
        viewModelScope.launch {
            uiState = try {
                val result = withContext(ioDispatcher) {
                    notifications.notifications().groupBy { it.created.toLocalDate() }
                }
                cachedNotifications.clear()
                cachedNotifications.putAll(result)
                UiState.Loaded(cachedNotifications)
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                UiState.Error(message = R.string.something_went_wrong)
            }
        }
    }

    fun deleteNotificationAsync(date: LocalDate, id: String): Deferred<Boolean> {
        return viewModelScope.async {
            try {
                withContext(ioDispatcher) { notifications.deleteById(id) }
                // @todo maybe add mutex to prevent data race when user deletes multiple notifications
                cachedNotifications.computeIfPresent(date) { _, notifications ->
                    val updatedNotifications = notifications.filterNot { it.id == id }
                    updatedNotifications.ifEmpty { null }
                }
                true
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                false
            }
        }
    }

    sealed class UiState {
        class Error(@StringRes val message: Int) : UiState()
        object Loading : UiState()
        class Loaded(
            val notifications: Map<LocalDate, List<ShortScheduleDiffNotification>>
        ) : UiState()
    }
}
