package com.github.plplmax.notifications.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.ui.alert.ProblemWithNotificationsDialog

@Composable
fun NavigationDrawerContent(
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
