package com.github.plplmax.notifications.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notifications.notification.ScheduleDiffNotification
import kotlinx.coroutines.launch
import java.util.Date

class NotificationViewModel : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Loading)
        private set

    init {
        initState()
    }

    private fun initState() {
        // @todo replace with real implementation
        viewModelScope.launch {
            uiState = UiState.Loaded(
                mapOf(
                    "Today" to listOf(
                        ScheduleDiffNotification("1", false, Date()),
                        ScheduleDiffNotification("2", true, Date()),
                    )
                )
            )
        }
    }

    fun deleteNotification(date: String, id: String) {
        val currentState = uiState
        if (currentState is UiState.Loaded) {
            val updatedMap = currentState.notifications.toMutableMap()
            updatedMap[date] = updatedMap[date]!!.filterNot { it.id == id }
            uiState = UiState.Loaded(updatedMap)
        }
    }

    sealed class UiState {
        object Loading : UiState()
        class Loaded(val notifications: Map<String, List<ScheduleDiffNotification>>) : UiState()
    }
}
