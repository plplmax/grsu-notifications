package com.github.plplmax.notifications.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.notification.ScheduleDiffNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.time.LocalDate

class NotificationViewModel(
    private val notifications: ScheduleNotifications,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    init {
        initState()
    }

    private fun initState() {
        viewModelScope.launch(ioDispatcher) {
            val result = notifications.notifications()
            val groupedByCreated = result.groupBy { it.created.toLocalDate() }
            uiState = UiState.Loaded(groupedByCreated)
        }
    }

    fun deleteNotificationAsync(date: LocalDate, id: String): Deferred<Boolean> {
        return viewModelScope.async(ioDispatcher) {
            try {
                notifications.deleteById(id)
                val currentState = uiState
                if (currentState is UiState.Loaded) {
                    // @todo maybe add mutex to prevent data race when user deletes multiple notifications
                    val updatedMap = currentState.notifications.toMutableMap()
                    updatedMap[date] =
                        updatedMap[date]?.filterNot { it.id == id } ?: return@async true
                    uiState = UiState.Loaded(updatedMap)
                }
                true
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                false
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        class Loaded(val notifications: Map<LocalDate, List<ScheduleDiffNotification>>) : UiState()
    }
}
