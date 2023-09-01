package com.github.plplmax.notifications.ui.alert

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.data.Constants

@Composable
fun ProblemWithNotificationsDialog(
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

private fun openDontKillMyApp(context: Context) {
    val url = "${Constants.DONT_KILL_BASE_URL}/${Constants.MANUFACTURER}" +
            "?app=${context.getString(R.string.app_name)}#user-solution"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
