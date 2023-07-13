package com.github.plplmax.notifications.ui.notification

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.notification.ScheduleDiffNotification
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme
import java.util.Date

@Composable
fun NotificationScreen() {
    val viewModel: NotificationViewModel = viewModel()
    when (val state = viewModel.uiState) {
        // @todo handle loading state
        is NotificationViewModel.UiState.Loaded -> NotificationContent(
            notifications = state.notifications,
            onDelete = viewModel::deleteNotification
        )

        else -> {}
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotificationContent(
    notifications: Map<String, List<ScheduleDiffNotification>> = mapOf(),
    onSelect: (id: String) -> Unit = {},
    onDelete: (date: String, id: String) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(all = 14.dp)
    ) {
        for ((date, notifs) in notifications) {
            if (notifs.isEmpty()) continue
            item(key = date) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.animateItemPlacement()
                )
            }
            items(items = notifs, key = { it.id }) { item ->
                NotificationCard(
                    item = item,
                    modifier = Modifier.animateItemPlacement(),
                    onSelect = { onSelect(item.id) },
                    onDelete = { onDelete(date, item.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    item: ScheduleDiffNotification,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    SwipeToDismissNotification(modifier = modifier, onDismiss = onDelete) {
        Card(
            onClick = onSelect,
            colors = colorsForNotificationCard(item.read)
        ) {
            Column(modifier = Modifier.padding(all = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NotificationIcon(item.read)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.schedule_changed))
                }
                Text(
                    "12:43 PM", // @todo replace with item.time
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .align(Alignment.End),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun colorsForNotificationCard(read: Boolean = false): CardColors {
    val disabledAlpha = 0.38f
    val containerColor = if (!read) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surfaceVariant
            .copy(alpha = disabledAlpha)
            .compositeOver(MaterialTheme.colorScheme.surface)
    }
    val contentColor = if (!read) {
        contentColorFor(containerColor)
    } else {
        contentColorFor(containerColor).copy(alpha = disabledAlpha)
    }
    return CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissNotification(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    dismissContent: @Composable RowScope.() -> Unit
) {
    val view = LocalView.current
    val dismissState = rememberDismissState()
    var firstComposition by remember { mutableStateOf(true) }

    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onDismiss()
    }

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        background = { SwipeToDismissBackground(dismissState) },
        dismissContent = dismissContent,
        directions = setOf(DismissDirection.EndToStart)
    )

    LaunchedEffect(dismissState.targetValue) {
        if (firstComposition) {
            firstComposition = false
            return@LaunchedEffect
        }
        @Suppress("DEPRECATION")
        view.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SwipeToDismissBackground(dismissState: DismissState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.DismissedToStart -> MaterialTheme.colorScheme.errorContainer
            else -> Color.Transparent
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .background(color)
            .padding(end = 14.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun NotificationIcon(read: Boolean = false) {
    Box {
        Icon(Icons.Default.Notifications, contentDescription = null)
        if (!read) {
            Box(
                modifier = Modifier
                    .padding(end = 2.dp)
                    .size(8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NotificationContentPreview() {
    GrsuNotificationsTheme {
        Surface {
            NotificationContent(
                notifications = mapOf(
                    "Today" to listOf(
                        ScheduleDiffNotification("1", false, Date()),
                        ScheduleDiffNotification("2", true, Date()),
                    )
                )
            )
        }
    }
}
