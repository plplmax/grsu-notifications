package com.github.plplmax.notifications.ui.snackbar

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun Snackbar(
    message: String,
    actionLabel: String? = null,
    onActionPerformed: () -> Unit = {}
) {
    val snackbarState = LocalSnackbarState.current
    LaunchedEffect(message, actionLabel, onActionPerformed) {
        val result = snackbarState.showSnackbar(message, actionLabel)
        if (result == SnackbarResult.ActionPerformed) onActionPerformed()
    }
}
