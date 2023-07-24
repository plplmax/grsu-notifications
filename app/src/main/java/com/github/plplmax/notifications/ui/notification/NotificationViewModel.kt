package com.github.plplmax.notifications.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.notification.ScheduleDiffNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

    fun deleteNotification(date: LocalDate, id: String) {
        val currentState = uiState
        if (currentState is UiState.Loaded) {
            val updatedMap = currentState.notifications.toMutableMap()
            updatedMap[date] = updatedMap[date]!!.filterNot { it.id == id }
            uiState = UiState.Loaded(updatedMap)
        }
    }

    sealed class UiState {
        object Loading : UiState()
        class Loaded(val notifications: Map<LocalDate, List<ScheduleDiffNotification>>) : UiState()
    }
}
