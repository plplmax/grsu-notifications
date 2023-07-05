package com.github.plplmax.notifications.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.plplmax.notifications.MainViewModel
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.UiState
import com.github.plplmax.notifications.data.Constants
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        LoginContent(
            state = viewModel.state,
            startUpdates = viewModel::startUpdates,
            stopUpdates = viewModel::stopUpdates,
            clearError = viewModel::clearError,
            needPermission = viewModel::needRequestNotificationsPermission,
            snackbarHostState = snackbarHostState
        )
    }

    Box(contentAlignment = Alignment.BottomCenter) {
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun LoginImage() {
    val orientation = LocalConfiguration.current.orientation
    val size = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        280.dp
    } else {
        180.dp
    }
    Image(
        painter = painterResource(id = R.drawable.login),
        contentDescription = null,
        modifier = Modifier.size(size)
    )
}

@Composable
private fun LoginContent(
    state: UiState = UiState.Initial(),
    startUpdates: (String) -> Unit = {},
    stopUpdates: () -> Unit = {},
    clearError: () -> Unit = {},
    needPermission: () -> Boolean = { false },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var login by rememberSaveable { mutableStateOf("") }
    var dialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val permissionLauncher = rememberPermissionLauncher(
        allowed = { startUpdates(login) },
        disallowed = {
            coroutineScope.launch {
                showNotificationPermissionSnackbar(snackbarHostState, context)
            }
        }
    )
    AlertDialogProblemWithNotifications(
        visible = dialogVisible,
        onDismissRequest = { dialogVisible = false },
        onConfirm = { dialogVisible = false }
    )
    LaunchedEffect(state) {
        login = when (state) {
            is UiState.Initial -> {
                state.login.ifEmpty { login }
            }

            is UiState.Updating -> {
                state.login
            }

            else -> login
        }

        if (needShowError(state, withSnackbar = true))
            snackbarHostState.showSnackbar(
                message = context.getString(state.id),
                actionLabel = context.getString(R.string.retry),
                duration = SnackbarDuration.Indefinite
            ).let { action ->
                if (action == SnackbarResult.ActionPerformed) {
                    startUpdates(login)
                }
            }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        LoginImage()
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = login,
            onValueChange = {
                login = it
                clearError()
            },
            label = { Text(text = stringResource(R.string.login)) },
            placeholder = {
                Text(
                    text = stringResource(R.string.enter_login)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                coroutineScope.launch {
                    if (isSubmitAvailable(state, login)) {
                        startUpdates(
                            requestPermissionLauncher = permissionLauncher,
                            snackbarHostState = snackbarHostState,
                            context = context,
                            startUpdates = { startUpdates(login) },
                            needPermission = needPermission
                        )
                    }
                }
            }),
            isError = needShowError(state),
            supportingText = {
                Text(
                    text = if (needShowError(state)) {
                        stringResource(state.id)
                    } else {
                        ""
                    }
                )
            },
            enabled = state !is UiState.Updating && state !is UiState.Loading
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    if (state is UiState.Updating) {
                        stopUpdates()
                    } else {
                        startUpdates(
                            requestPermissionLauncher = permissionLauncher,
                            snackbarHostState = snackbarHostState,
                            context = context,
                            startUpdates = { startUpdates(login) },
                            needPermission = needPermission
                        )
                    }
                }
            },
            enabled = isSubmitAvailable(state, login),
            colors = kotlin.run {
                val containerColor = if (state is UiState.Updating) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
                val contentColor = contentColorFor(backgroundColor = containerColor)
                ButtonDefaults.buttonColors(containerColor, contentColor)
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(
                        if (state is UiState.Updating) {
                            R.string.stop_updates
                        } else {
                            R.string.start_updates
                        }
                    )
                )
            }
        }

        TextButton(onClick = { dialogVisible = true }) {
            Text(text = stringResource(R.string.have_problems_getting_notifications))
        }
    }
}

@Composable
private fun AlertDialogProblemWithNotifications(
    visible: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    val context = LocalContext.current
    if (visible) {
        AlertDialog(
            text = {
                Surface(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = stringResource(R.string.problems_getting_notifications_dialog))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(R.string.back))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    openDontKillMyApp(context)
                }) {
                    Text(text = stringResource(R.string._continue))
                }
            },
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
private fun rememberPermissionLauncher(
    allowed: () -> Unit = {},
    disallowed: () -> Unit = {}
): ActivityResultLauncher<String> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                allowed()
            } else {
                disallowed()
            }
        }
    )
}

private suspend fun startUpdates(
    requestPermissionLauncher: ActivityResultLauncher<String>,
    snackbarHostState: SnackbarHostState,
    context: Context,
    startUpdates: () -> Unit,
    needPermission: () -> Boolean
) {
    if (needPermission()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showNotificationPermissionSnackbar(snackbarHostState, context)
        }
    } else {
        startUpdates()
    }
}

private suspend fun showNotificationPermissionSnackbar(
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    snackbarHostState.showSnackbar(
        message = context.getString(R.string.app_cant_work_without_notifications),
        actionLabel = context.getString(R.string.settings),
        duration = SnackbarDuration.Long
    ).let { action ->
        if (action == SnackbarResult.ActionPerformed) {
            openDeviceNotificationSettings(context)
        }
    }
}

private fun openDeviceNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        Intent(Settings.ACTION_SETTINGS)
    }
    ContextCompat.startActivity(context, intent, null)
}

@OptIn(ExperimentalContracts::class)
private fun needShowError(state: UiState, withSnackbar: Boolean = false): Boolean {
    contract {
        returns(true) implies (state is UiState.Failure)
    }
    return state is UiState.Failure && state.showSnackbar == withSnackbar
}

private fun isSubmitAvailable(state: UiState, login: String): Boolean {
    return state !is UiState.Loading && !needShowError(state) && login.isNotBlank()
}

private fun openDontKillMyApp(context: Context) {
    val url =
        "${Constants.DONT_KILL_BASE_URL}/${Constants.MANUFACTURER}?app=${context.getString(R.string.app_name)}#user-solution"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LoginContentPreview() {
    GrsuNotificationsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginContent()
        }
    }
}
