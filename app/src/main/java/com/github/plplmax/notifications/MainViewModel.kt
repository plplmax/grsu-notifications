package com.github.plplmax.notifications

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.github.plplmax.notifications.centre.NotificationCentre
import com.github.plplmax.notifications.data.Errors
import com.github.plplmax.notifications.data.user.Users
import com.github.plplmax.notifications.data.worker.ScheduleWorker
import com.github.plplmax.notifications.ui.navigation.Routes
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val users: Users,
    private val notificationCentre: NotificationCentre,
    private val workManager: WorkManager
) : ViewModel() {
    var state: UiState by mutableStateOf(UiState.Initial())
        private set

    val needRequestNotificationsPermission: Boolean
        get() = !notificationCentre.hasNotificationsPermission

    var startDestination: Routes by mutableStateOf(Routes.Undefined)
        private set

    init {
        initState()
    }

    private fun initState() {
        viewModelScope.launch {
            val userId = users.id()
            val login = users.login()

            if (userId == 0) {
                startDestination = Routes.Welcome
                state = UiState.Initial(login)
            } else {
                startDestination = Routes.Notifications
                state = UiState.Success
            }
        }
    }

    fun signIn(login: String) {
        state = UiState.Loading
        viewModelScope.launch {
            val userId = users.idByLogin(login)
            userId.onFailure { state = stateForError(it) }
            userId.onSuccess { id ->
                users.saveId(id)
                users.saveLogin(login)
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                state = try {
                    workManager.enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
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

    private fun stateForError(error: Throwable): UiState {
        return when (val stringRes = stringResourceForError(error)) {
            R.string.check_internet_connection -> UiState.Failure(stringRes, showSnackbar = true)
            R.string.something_went_wrong -> UiState.Failure(stringRes, showSnackbar = true)
            else -> UiState.Failure(stringRes)
        }
    }

    @StringRes
    private fun stringResourceForError(error: Throwable): Int = when (error.message) {
        Errors.CHECK_INTERNET_CONNECTION.toString() -> R.string.check_internet_connection
        Errors.INVALID_LOGIN.toString() -> R.string.invalid_login
        else -> R.string.something_went_wrong
    }

    fun clearError() {
        state = UiState.Initial()
    }

    companion object {
        private const val WORK_NAME = "ScheduleUpdate"
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
    class Initial(val login: String = "") : UiState()
    object Loading : UiState()
    object Success : UiState()
    class Failure(@StringRes val id: Int, val showSnackbar: Boolean = false) : UiState()
}
