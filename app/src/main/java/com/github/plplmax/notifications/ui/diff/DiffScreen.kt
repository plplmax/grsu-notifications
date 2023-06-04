package com.github.plplmax.notifications.ui.diff

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.plplmax.notifications.data.schedule.models.Day
import com.github.plplmax.notifications.data.schedule.models.DiffedSchedule
import com.github.plplmax.notifications.data.schedule.models.Lesson
import com.github.plplmax.notifications.data.schedule.models.Teacher
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme

@Composable
fun DiffScreen(viewModel: DiffViewModel = viewModel()) {
    // @todo move id to the function's parameters
    val id = "test-id"
    LaunchedEffect(id) {
        viewModel.loadScheduleById(id)
    }
    when (val state = viewModel.state) {
        // @todo show spinner while loading
        is DiffViewModel.UiState.Loaded -> DiffContent(state.schedule)
        is DiffViewModel.UiState.Error -> Text("Error occurred: ${state.text}")
        is DiffViewModel.UiState.Loading -> Text("Loading...")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiffContent(schedule: DiffedSchedule) {
    // @todo make synchronization between tabs and pager
    var selectedTab by remember { mutableStateOf(0) }
    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 24.dp,
            modifier = Modifier.padding(14.dp)
        ) {
            Tab(selected = 0 == selectedTab, onClick = { selectedTab = 0 }) {
                Text(text = "Test")
            }
            Tab(selected = 1 == selectedTab, onClick = { selectedTab = 1 }) {
                Text(text = "Test")
            }
            Tab(selected = 2 == selectedTab, onClick = { selectedTab = 2 }) {
                Text(text = "Test")
            }
        }
        HorizontalPager(
            pageCount = schedule.days.size,
            contentPadding = PaddingValues(14.dp),
            pageSpacing = 4.dp,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) { index ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                schedule.days[index].lessons.forEach {
                    ScheduleCard(it)
                }
            }
        }
    }
}

@Composable
fun ScheduleCard(lesson: Lesson) {
    val cardBackground = if (lesson.isAdded) {
        Color.Green.copy(alpha = 0.2f)
    } else {
        Color.Red.copy(alpha = 0.2f)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
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
                DiffText(text = lesson.title)
                DiffText(
                    text = lesson.teacher.fullname,
                    style = MaterialTheme.typography.titleSmall
                )
                DiffText(
                    text = "${lesson.address}, ${lesson.room}",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Column(modifier = Modifier.weight(0.3f)) {
                DiffText(
                    text = lesson.type,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
fun DiffText(text: String, style: TextStyle = LocalTextStyle.current) {
    val color = when (text.first()) {
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
    Text(text = text, style = style, modifier = Modifier.background(color))
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

private val someSchedule = DiffedSchedule(
    days = listOf(
        Day(
            "11.05.2015", lessons = listOf(
                Lesson(
                    "11:40",
                    "13:00",
                    Teacher("Maksim Ploski", "Android Developer"),
                    "Examination (written)",
                    "Elegant Object principles",
                    "Ozheshko 22",
                    "316",
                    isAdded = false,
                    isDeleted = true
                ),
                Lesson(
                    "18:15",
                    "19:35",
                    Teacher("Maksim Ploski", "Android Developer"),
                    "Examination (oral)",
                    "Elegant Object principles",
                    "Ozheshko 22",
                    "316",
                    isAdded = true,
                    isDeleted = false
                )
            ).sortedBy { it.timeStart }
        )
    )
)
