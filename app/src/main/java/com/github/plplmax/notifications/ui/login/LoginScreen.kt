package com.github.plplmax.notifications.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.github.plplmax.notifications.ui.snackbar.LocalSnackbarState
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@Composable
fun LoginScreen(viewModel: MainViewModel, onSuccess: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        LoginContent(
            state = viewModel.state,
            signIn = viewModel::signIn,
            onSuccess = onSuccess,
            clearError = viewModel::clearError,
            needPermission = viewModel::needRequestNotificationsPermission
        )
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
    signIn: (String) -> Unit = {},
    onSuccess: () -> Unit = {},
    clearError: () -> Unit = {},
    needPermission: () -> Boolean = { false }
) {
    var login by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarState.current
    val coroutineScope = rememberCoroutineScope()
    val permissionLauncher = rememberPermissionLauncher(
        allowed = { signIn(login) },
        disallowed = {
            coroutineScope.launch {
                showNotificationPermissionSnackbar(snackbarHostState, context)
            }
        }
    )
    LaunchedEffect(state) {
        login = when (state) {
            is UiState.Initial -> {
                state.login.ifEmpty { login }
            }

            else -> login
        }

        if (state is UiState.Success) onSuccess()
        if (needShowError(state, withSnackbar = true))
            snackbarHostState.showSnackbar(
                message = context.getString(state.id),
                actionLabel = context.getString(R.string.retry),
                duration = SnackbarDuration.Indefinite
            ).let { action ->
                if (action == SnackbarResult.ActionPerformed) {
                    signIn(login)
                }
            }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .padding(all = 14.dp)
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
                        signIn(
                            requestPermissionLauncher = permissionLauncher,
                            snackbarHostState = snackbarHostState,
                            context = context,
                            signIn = { signIn(login) },
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
            enabled = state !is UiState.Loading
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    signIn(
                        requestPermissionLauncher = permissionLauncher,
                        snackbarHostState = snackbarHostState,
                        context = context,
                        signIn = { signIn(login) },
                        needPermission = needPermission
                    )
                }
            },
            enabled = isSubmitAvailable(state, login),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = stringResource(R.string.sign_in))
            }
        }
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

private suspend fun signIn(
    requestPermissionLauncher: ActivityResultLauncher<String>,
    snackbarHostState: SnackbarHostState,
    context: Context,
    signIn: () -> Unit,
    needPermission: () -> Boolean
) {
    if (needPermission()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showNotificationPermissionSnackbar(snackbarHostState, context)
        }
    } else {
        signIn()
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

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LoginContentPreview() {
    GrsuNotificationsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginContent()
        }
    }
}
