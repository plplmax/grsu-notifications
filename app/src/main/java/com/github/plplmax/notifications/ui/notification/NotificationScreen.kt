package com.github.plplmax.notifications.ui.notification

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.data.notification.models.ShortScheduleDiffNotification
import com.github.plplmax.notifications.ui.navigation.Routes
import com.github.plplmax.notifications.ui.snackbar.LocalSnackbarState
import com.github.plplmax.notifications.ui.snackbar.Snackbar
import com.github.plplmax.notifications.ui.text.DateText
import com.github.plplmax.notifications.ui.text.TimeText
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.LocalTime
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

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = koinViewModel(),
    onSelect: (id: String) -> Unit = {},
    showNavigation: () -> Unit
) {
    val notifications = viewModel.paging.collectAsLazyPagingItems()

    Column {
        NotificationTopAppBar(showNavigation)
        NotificationContent(
            notifications = notifications,
            onSelect = { id: String ->
                viewModel.readNotification(id) // @todo move reading notification to the DiffScreen
                onSelect(id)
            },
            onDelete = { id: String -> viewModel.deleteNotificationAsync(id).await() }
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.failed_to_load_notifications))
        Spacer(modifier = Modifier.height(6.dp))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotificationContent(
    notifications: LazyPagingItems<ShortScheduleDiffNotification>,
    onSelect: (id: String) -> Unit = { _ -> },
    onDelete: suspend (id: String) -> Boolean = { true }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(all = 14.dp)
    ) {
        var lastShownDate: LocalDate? = null

        repeat(times = notifications.itemCount) { index ->
            val notification = notifications.peek(index) ?: kotlin.run {
                item(key = notifications.itemKey { it.id }(index)) {
                    NotificationCard(item = notifications[index])
                }
                return@repeat
            }
            val date = notification.created.toLocalDate()

            if (date != lastShownDate) {
                item(key = date) {
                    DateText(
                        date = date,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
                lastShownDate = date
            }

            item(key = notification.id) {
                val notificationItem = notifications[index] ?: return@item
                SwipeToDismissNotification(
                    modifier = Modifier.animateItemPlacement(),
                    onDismiss = { onDelete(notificationItem.id) }
                ) {
                    NotificationCard(
                        item = notificationItem,
                        onSelect = { onSelect(notificationItem.id) })
                }
            }
        }

        if (notifications.itemCount == 0) {
            when (notifications.loadState.refresh) {
                is LoadState.Loading -> {
                    repeat(times = 10) {
                        item { NotificationCard() }
                    }
                }

                is LoadState.NotLoading -> {
                    item { NoNotificationsText(modifier = Modifier.fillParentMaxSize()) }
                }

                is LoadState.Error -> {
                    item {
                        ErrorContent(
                            modifier = Modifier.fillParentMaxSize(),
                            onRetry = notifications::retry
                        )
                    }
                }
            }
        }
    }

    val state = notifications.loadState
    if (state.prepend is LoadState.Error ||
        state.append is LoadState.Error ||
        state.refresh is LoadState.Error && notifications.itemCount > 0
    ) {
        Snackbar(
            message = stringResource(R.string.failed_to_load_notifications),
            actionLabel = stringResource(R.string.retry),
            onActionPerformed = notifications::retry
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    item: ShortScheduleDiffNotification? = null,
    onSelect: () -> Unit = {}
) {
    val placeholderModifier = Modifier.placeholder(
        visible = item == null,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
        highlight = PlaceholderHighlight.fade(
            highlightColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
        )
    )
    Card(
        onClick = onSelect,
        colors = colorsForNotificationCard(item?.read ?: false)
    ) {
        Column(modifier = Modifier.padding(all = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NotificationIcon(modifier = placeholderModifier, read = item?.read ?: false)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.schedule_changed),
                    modifier = placeholderModifier
                )
            }
            TimeText(
                time = item?.created?.toLocalTime() ?: LocalTime.now(),
                modifier = Modifier
                    .padding(top = 14.dp)
                    .align(Alignment.End)
                    .then(placeholderModifier)
            )
        }
    }
}

@Composable
private fun NoNotificationsText(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.no_notifications_yet),
            textAlign = TextAlign.Center
        )
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
private fun NotificationIcon(modifier: Modifier = Modifier, read: Boolean = false) {
    Box(modifier = modifier) {
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

@Preview
@Composable
private fun NotificationContentPreview() {
    val items = flowOf(
        PagingData.from(
            data = notificationsPreview,
            sourceLoadStates = LoadStates(
                LoadState.NotLoading(false),
                LoadState.NotLoading(false),
                LoadState.NotLoading(false)
            )
        )
    ).collectAsLazyPagingItems()
    GrsuNotificationsTheme {
        Surface {
            NotificationContent(notifications = items)
        }
    }
}

private val notificationsPreview = List(size = 10) { index ->
    ShortScheduleDiffNotification(
        id = "$index",
        read = index % 2 == 0,
        created = ZonedDateTime.now().minusDays(index.toLong())
    )
}
