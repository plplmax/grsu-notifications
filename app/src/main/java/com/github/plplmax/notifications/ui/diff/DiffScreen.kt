package com.github.plplmax.notifications.ui.diff

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.data.schedule.enums.ModificationType
import com.github.plplmax.notifications.data.schedule.models.Day
import com.github.plplmax.notifications.data.schedule.models.Lesson
import com.github.plplmax.notifications.data.schedule.models.ScheduleDiff
import com.github.plplmax.notifications.data.schedule.models.Teacher
import com.github.plplmax.notifications.ui.progress.ProgressIndicator
import com.github.plplmax.notifications.ui.text.DateText
import com.github.plplmax.notifications.ui.text.TimeText
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DiffScreen(viewModel: DiffViewModel = koinViewModel(), id: String, onBack: () -> Unit) {
    LaunchedEffect(id) {
        viewModel.loadScheduleById(id)
    }
    Column {
        DiffTopAppBar(state = viewModel.state, onBack = onBack)
        when (val state = viewModel.state) {
            is DiffViewModel.UiState.Loaded -> DiffContent(state.notification.diff)
            is DiffViewModel.UiState.Error -> Text("Error occurred: ${state.text}")
            is DiffViewModel.UiState.Loading -> ProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiffTopAppBar(state: DiffViewModel.UiState, onBack: () -> Unit) {
    TopAppBar(
        title = {
            if (state is DiffViewModel.UiState.Loaded) {
                Row {
                    DateText(date = state.notification.created.toLocalDate())
                    Text(text = ", ")
                    TimeText(time = state.notification.created.toLocalTime())
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiffContent(schedule: ScheduleDiff) {
    if (schedule.days.isEmpty()) {
        Text(text = "There are no differences")
        return
    }
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column {
        ScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
            schedule.days.forEachIndexed { index, day ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                ) { TabContent(date = LocalDate.parse(day.date)) }
            }
        }
        HorizontalPager(
            pageCount = schedule.days.size,
            state = pagerState
        ) { index ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(schedule.days[index].lessons) { ScheduleCard(it) }
            }
        }
    }
}

@Composable
fun TabContent(date: LocalDate) {
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0]!!
    val formatter = DateTimeFormatter.ofPattern("EE, dd.MM", locale)
    Text(
        text = date.format(formatter).uppercase(locale),
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun ScheduleCard(lesson: Lesson) {
    val cardBackground = when (lesson.modificationType) {
        ModificationType.Added -> Color.Green.copy(alpha = 0.2f)
        ModificationType.Deleted -> Color.Red.copy(alpha = 0.2f)
        else -> Color.Unspecified
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .background(cardBackground)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DiffText(text = lesson.timeStart)
                DiffText(text = lesson.timeEnd)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                DiffText(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(lineHeight = 20.sp)
                )
                DiffText(
                    text = lesson.teacher.fullname,
                    style = MaterialTheme.typography.bodyMedium
                )
                DiffText(
                    text = lesson.fullAddress,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(modifier = Modifier.weight(0.2f)) {
                DiffText(
                    text = lesson.type,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.End),
                    textAlign = TextAlign.Right,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun DiffText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val color = when (text.firstOrNull()) {
        '+' -> {
            Color.Green.copy(alpha = 0.2f)
        }

        '-' -> {
            Color.Red.copy(alpha = 0.2f)
        }

        else -> {
            Color.Unspecified
        }
    }
    Text(text = text, modifier = modifier.background(color), textAlign = textAlign, style = style)
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DiffContentPreview() {
    GrsuNotificationsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DiffContent(someSchedule)
        }
    }
}

private val someSchedule = ScheduleDiff(
    days = listOf(
        Day(
            "2015-05-11", lessons = listOf(
                Lesson(
                    "11:40",
                    "13:00",
                    Teacher("Maksim Ploski"),
                    "Examination (written)",
                    "Elegant Object principles",
                    "Ozheshko 22",
                    "316",
                    modificationType = ModificationType.Deleted
                ),
                Lesson(
                    "18:15",
                    "19:35",
                    Teacher("Maksim Ploski"),
                    "Examination (oral)",
                    "Elegant Object principles",
                    "Ozheshko 22",
                    "316",
                    modificationType = ModificationType.Added
                )
            ).sortedBy { it.timeStart }
        )
    )
)
