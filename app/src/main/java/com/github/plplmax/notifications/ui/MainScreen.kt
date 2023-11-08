package com.github.plplmax.notifications.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.plplmax.notifications.MainViewModel
import com.github.plplmax.notifications.ui.navigation.AppNavHost
import com.github.plplmax.notifications.ui.navigation.AppNavigationDrawer
import com.github.plplmax.notifications.ui.snackbar.LocalSnackbarState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    AppNavigationDrawer(
        viewModel = viewModel,
        navController = navController,
        drawerState = drawerState
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = LocalSnackbarState.current) }
        ) { padding ->
            AppNavHost(
                viewModel = viewModel,
                modifier = Modifier.padding(padding),
                navController = navController,
                showNavigation = { scope.launch { drawerState.open() } }
            )
        }
    }
}
