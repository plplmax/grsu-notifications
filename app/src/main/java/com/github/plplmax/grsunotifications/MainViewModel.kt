package com.github.plplmax.grsunotifications

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.github.plplmax.grsunotifications.data.Errors
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.data.workManager.ScheduleWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val userRepository: UserRepository,
    private val workManager: WorkManager
) : ViewModel() {
    var state: UiState by mutableStateOf(UiState.Initial)
        private set

    fun startUpdates(login: String) {
        state = UiState.Loading
        viewModelScope.launch {
            val userId = userRepository.idByLogin(login)
            userId.onFailure { state = UiState.Failure(stringResourceForError(it)) }
            userId.onSuccess { id ->
                userRepository.saveId(id)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                state = try {
                    workManager.enqueueUniquePeriodicWork(
                        "ScheduleUpdate",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        PeriodicWorkRequestBuilder<ScheduleWorker>(
                            30, TimeUnit.MINUTES
                        ).setConstraints(constraints).build()
                    ).await()
                    UiState.Success
                } catch (e: Exception) {
                    UiState.Failure(R.string.something_went_wrong)
                }
            }
        }
    }

    @StringRes
    private fun stringResourceForError(error: Throwable): Int = when (error.message) {
        Errors.CHECK_INTERNET_CONNECTION.toString() -> R.string.check_internet_connection
        Errors.INVALID_LOGIN.toString() -> R.string.invalid_login
        else -> R.string.something_went_wrong
    }

    fun clearError() {
        state = UiState.Initial
    }
}

fun <T : ViewModel> T.createFactory(): ViewModelProvider.Factory {
    val viewModel = this
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModel as T
    }
}

sealed class UiState {
    object Initial : UiState()
    object Success : UiState()
    object Loading : UiState()
    class Failure(@StringRes val id: Int) : UiState()
}
