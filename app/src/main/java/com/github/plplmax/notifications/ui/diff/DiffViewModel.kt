package com.github.plplmax.notifications.ui.diff

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.plplmax.notifications.data.diffedSchedule.DiffedScheduleRepository
import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

class DiffViewModel(
    private val scheduleRepository: DiffedScheduleRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    var state: UiState by mutableStateOf(UiState.Loading)
        private set

    fun loadScheduleById(id: String) {
        viewModelScope.launch(ioDispatcher) {
            state = try {
                val schedule = scheduleRepository.scheduleById(id)
                val newState = if (schedule.isEmpty()) {
                    // @todo replace string literal with the constant
                    UiState.Error("Schedule not found")
                } else {
                    UiState.Loaded(schedule.first())
                }
                newState
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                UiState.Error("Something went wrong")
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val schedule: DiffedSchedule) : UiState()
        data class Error(val text: String) : UiState()
    }
}
