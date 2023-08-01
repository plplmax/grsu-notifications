package com.github.plplmax.notifications.ui.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarState: ProvidableCompositionLocal<SnackbarHostState> =
    staticCompositionLocalOf { SnackbarHostState() }
