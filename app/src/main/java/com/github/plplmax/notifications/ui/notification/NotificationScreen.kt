package com.github.plplmax.notifications.ui.notification

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.plplmax.notifications.App
import com.github.plplmax.notifications.MainActivity
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.ui.navigation.Routes
import com.github.plplmax.notifications.ui.progress.ProgressIndicator
import com.github.plplmax.notifications.ui.refresh.PullRefreshIndicator
import com.github.plplmax.notifications.ui.refresh.pullRefresh
import com.github.plplmax.notifications.ui.refresh.rememberPullRefreshState
import com.github.plplmax.notifications.ui.snackbar.LocalSnackbarState
import com.github.plplmax.notifications.ui.snackbar.Snackbar
import com.github.plplmax.notifications.ui.text.DateText
import com.github.plplmax.notifications.ui.text.TimeText
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme
import java.time.LocalDate
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopAppBar(showNavigation: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(Routes.Notifications.title)) },
        navigationIcon = {
            IconButton(onClick = showNavigation) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NotificationScreen(onSelect: (id: String) -> Unit = {}, showNavigation: () -> Unit) {
    val context = LocalContext.current
    val app = remember(context) {
        ((context as MainActivity).application) as App
    }
    val viewModel = viewModel(initializer = {
        NotificationViewModel(notifications = app.deps.scheduleNotifications)
    })
    Column {
        NotificationTopAppBar(showNavigation)
        AnimatedContent(targetState = viewModel.uiState) { state ->
            when (state) {
                is NotificationViewModel.UiState.Loaded -> NotificationContent(
                    notifications = state.notifications,
                    onSelect = { date, id ->
                        viewModel.readNotification(date, id)
                        onSelect(id)
                    },
                    onDelete = { date, id -> viewModel.deleteNotificationAsync(date, id).await() },
                    onRefresh = viewModel::loadNotifications
                )

                is NotificationViewModel.UiState.Loading -> ProgressIndicator()
                is NotificationViewModel.UiState.Error -> ErrorContent(
                    message = stringResource(state.message),
                    onRetry = viewModel::loadNotifications
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.failed_to_load_notifications))
    }
    Snackbar(
        message = message,
        actionLabel = stringResource(R.string.retry),
        onActionPerformed = onRetry
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotificationContent(
    notifications: State<Map<LocalDate, List<ShortScheduleDiffNotification>>>,
    onSelect: (date: LocalDate, id: String) -> Unit = { _, _ -> },
    onDelete: suspend (date: LocalDate, id: String) -> Boolean = { _, _ -> true },
    onRefresh: () -> Unit = {}
) {
    PullToRefresh(onRefresh = onRefresh) {
        if (notifications.value.isEmpty()) {
            NoNotificationsText()
            return@PullToRefresh
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(all = 14.dp)
        ) {
            for ((date, notifs) in notifications.value) {
                if (notifs.isEmpty()) continue
                // @todo maybe invoke date.toString() as key
                item(key = date) {
                    DateText(
                        date = date,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
                items(items = notifs, key = { it.id }) { item ->
                    NotificationCard(
                        item = item,
                        modifier = Modifier.animateItemPlacement(),
                        onSelect = { onSelect(date, item.id) },
                        onDelete = { onDelete(date, item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PullToRefresh(
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = onRefresh)

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(
            refreshing = false,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun NoNotificationsText() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.no_notifications_yet))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    item: ShortScheduleDiffNotification,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {},
    onDelete: suspend () -> Boolean = { true }
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
                TimeText(
                    time = item.created.toLocalTime(),
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .align(Alignment.End)
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
    onDismiss: suspend () -> Boolean = { true },
    dismissContent: @Composable RowScope.() -> Unit
) {
    val view = LocalView.current
    val dismissState = rememberDismissState(positionalThreshold = { total -> total / 2 })
    var firstComposition by remember { mutableStateOf(true) }
    val snackbarState = LocalSnackbarState.current
    val context = LocalContext.current

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

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
            val success = onDismiss()
            if (!success) {
                val error = context.getString(R.string.something_went_wrong)
                snackbarState.showSnackbar(message = error)
                dismissState.reset()
            }
        }
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
                    .size(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(all = 2.dp)
                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
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
                notifications = remember {
                    mutableStateOf(
                        mapOf(
                            LocalDate.now() to listOf(
                                ShortScheduleDiffNotification(
                                    "1",
                                    false,
                                    ZonedDateTime.now().minusHours(5)
                                ),
                                ShortScheduleDiffNotification("2", true, ZonedDateTime.now()),
                            ),
                            LocalDate.now().minusDays(1) to listOf(
                                ShortScheduleDiffNotification("3", false, ZonedDateTime.now()),
                                ShortScheduleDiffNotification("4", true, ZonedDateTime.now()),
                            ),
                            LocalDate.now().minusDays(2) to listOf(
                                ShortScheduleDiffNotification("5", false, ZonedDateTime.now()),
                            ),
                            LocalDate.now().minusYears(1) to listOf(
                                ShortScheduleDiffNotification("6", false, ZonedDateTime.now()),
                            )
                        )
                    )
                }
            )
        }
    }
}
