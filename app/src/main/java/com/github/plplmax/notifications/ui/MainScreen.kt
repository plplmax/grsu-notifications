package com.github.plplmax.notifications.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.plplmax.notifications.MainViewModel
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.UiState
import com.github.plplmax.notifications.ui.navigation.AppNavHost
import com.github.plplmax.notifications.ui.navigation.NavigationDrawerContent
import com.github.plplmax.notifications.ui.navigation.Routes
import com.github.plplmax.notifications.ui.snackbar.LocalSnackbarState
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val backStack by navController.currentBackStackEntryAsState()
    val gesturesEnabled by remember {
        derivedStateOf { backStack?.destination?.route == Routes.Notifications.route }
    }
    val snackbarState = LocalSnackbarState.current
    val context = LocalContext.current
    ModalNavigationDrawer(
        drawerContent = {
            NavigationDrawerContent(
                login = (viewModel.state as? UiState.Success)?.login ?: "",
                currentRoute = backStack?.destination?.route ?: "",
                openNotifications = {
                    scope.launch {
                        navController.navigate(Routes.Notifications.route) {
                            launchSingleTop = true
                        }
                        drawerState.close()
                    }
                },
                onSignOut = {
                    scope.launch {
                        val signedOut = viewModel.signOut()
                        drawerState.close()
                        if (!signedOut.await()) {
                            snackbarState.showSnackbar(context.getString(R.string.something_went_wrong))
                        }
                    }
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled
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
