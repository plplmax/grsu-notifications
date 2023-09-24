package com.github.plplmax.notifications.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.plplmax.notifications.MainViewModel
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.UiState
import com.github.plplmax.notifications.ui.alert.ProblemWithNotificationsDialog
import com.github.plplmax.notifications.ui.snackbar.LocalSnackbarState
import kotlinx.coroutines.launch

@Composable
fun AppNavigationDrawer(
    viewModel: MainViewModel,
    navController: NavController,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val backStack by navController.currentBackStackEntryAsState()
    val gesturesEnabled by remember {
        derivedStateOf { backStack?.destination?.route == Routes.Notifications.route }
    }
    val snackbarState = LocalSnackbarState.current
    val context = LocalContext.current
    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
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
                            val message = context.getString(R.string.something_went_wrong)
                            snackbarState.showSnackbar(message)
                        }
                    }
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        content = content
    )
}

@Composable
private fun DrawerContent(
    login: String,
    currentRoute: String,
    openNotifications: () -> Unit,
    onSignOut: () -> Unit
) {
    var showHelpDialog by remember { mutableStateOf(false) }
    ModalDrawerSheet {
        Text(text = login, modifier = Modifier.padding(24.dp))
        Divider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
        Spacer(modifier = Modifier.height(8.dp))
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 8.dp),
            label = { Text(text = stringResource(R.string.notifications)) },
            selected = currentRoute == Routes.Notifications.route,
            onClick = openNotifications,
            icon = {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 8.dp),
            label = { Text(text = stringResource(R.string.sign_out)) },
            selected = false,
            onClick = onSignOut,
            icon = {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
            }
        )
        Spacer(modifier = Modifier.height(6.dp))
        Divider(color = MaterialTheme.colorScheme.background, thickness = 2.dp)
        TextButton(
            modifier = Modifier.padding(start = 12.dp),
            onClick = { showHelpDialog = true }
        ) {
            Text(text = stringResource(R.string.have_problems_getting_notifications))
        }
        ProblemWithNotificationsDialog(
            visible = showHelpDialog,
            onDismissRequest = { showHelpDialog = false },
            onConfirm = { showHelpDialog = false }
        )
    }
}
