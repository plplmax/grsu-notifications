package com.github.plplmax.grsunotifications

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.EXTRA_APP_PACKAGE
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.work.WorkManager
import com.github.plplmax.grsunotifications.ui.theme.GrsuNotificationsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels(factoryProducer = {
        val deps = (application as App).deps
        MainViewModel(
            deps.userRepository,
            deps.scheduleRepository,
            deps.notificationCentre,
            WorkManager.getInstance(applicationContext)
        ).createFactory()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrsuNotificationsTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Form(viewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.needRequestNotificationsPermission) {
            viewModel.stopUpdates()
        }
    }
}

@SuppressLint("InlinedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Form(viewModel: MainViewModel) {
    var login by rememberSaveable {
        mutableStateOf("")
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = viewModel.state, block = {
        viewModel.state.let { state ->
            login = when (state) {
                is UiState.Initial -> {
                    state.login.ifEmpty { login }
                }
                is UiState.Updating -> {
                    state.login
                }
                else -> login
            }
        }
    })

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.startUpdates(login)
            } else {
                coroutineScope.launch {
                    showNotificationPermissionSnackbar(
                        snackbarHostState,
                        context
                    )
                }
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 14.dp)
    ) {
        TextField(
            value = login,
            onValueChange = {
                login = it
                viewModel.clearError()
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
                if (isSubmitAvailable(viewModel.state, login)) {
                    startUpdates(
                        viewModel,
                        requestPermissionLauncher,
                        coroutineScope,
                        snackbarHostState,
                        context,
                        login
                    )
                }
            }),
            isError = needShowError(viewModel.state),
            supportingText = {
                Text(
                    text = viewModel.state.let { state ->
                        if (needShowError(state)) {
                            stringResource(state.id)
                        } else {
                            ""
                        }
                    }
                )
            },
            enabled = viewModel.state !is UiState.Updating && viewModel.state !is UiState.Loading
        )
        Button(
            onClick = {
                if (viewModel.state is UiState.Updating) {
                    viewModel.stopUpdates()
                } else {
                    startUpdates(
                        viewModel,
                        requestPermissionLauncher,
                        coroutineScope,
                        snackbarHostState,
                        context,
                        login
                    )
                }
            },
            modifier = Modifier.padding(top = 14.dp),
            enabled = isSubmitAvailable(viewModel.state, login),
            colors = kotlin.run {
                val containerColor = if (viewModel.state is UiState.Updating) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
                val contentColor = contentColorFor(backgroundColor = containerColor)
                ButtonDefaults.buttonColors(containerColor, contentColor)
            }
        ) {
            Row(modifier = Modifier.animateContentSize()) {
                if (viewModel.state is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(
                            if (viewModel.state is UiState.Updating) {
                                R.string.stop_updates
                            } else {
                                R.string.start_updates
                            }
                        )
                    )
                }
            }
        }

        LaunchedEffect(viewModel.state) {
            val state = viewModel.state
            if (needShowError(state, withSnackbar = true))
                snackbarHostState.showSnackbar(
                    message = context.getString(state.id),
                    actionLabel = context.getString(R.string.retry),
                    duration = SnackbarDuration.Indefinite
                ).let { action ->
                    if (action == SnackbarResult.ActionPerformed) {
                        viewModel.startUpdates(login)
                    }
                }
        }
    }

    Box(contentAlignment = Alignment.BottomCenter) {
        SnackbarHost(hostState = snackbarHostState)
    }
}

private fun startUpdates(
    viewModel: MainViewModel,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context,
    login: String
) {
    if (viewModel.needRequestNotificationsPermission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            coroutineScope.launch {
                showNotificationPermissionSnackbar(
                    snackbarHostState,
                    context
                )
            }
        }
    } else {
        viewModel.startUpdates(login)
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
            putExtra(EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        Intent(Settings.ACTION_SETTINGS)
    }
    startActivity(context, intent, null)
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

@Preview
@Composable
private fun FormPreview() {
    GrsuNotificationsTheme {
//        Form()
    }
}
