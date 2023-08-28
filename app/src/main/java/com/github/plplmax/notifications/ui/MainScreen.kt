package com.github.plplmax.notifications.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.plplmax.notifications.MainViewModel
import com.github.plplmax.notifications.ui.navigation.AppNavHost
import com.github.plplmax.notifications.ui.snackbar.LocalSnackbarState

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = LocalSnackbarState.current) }
    ) { padding ->
        AppNavHost(
            viewModel = viewModel,
            modifier = Modifier.padding(padding),
            navController = navController
        )
    }
}
