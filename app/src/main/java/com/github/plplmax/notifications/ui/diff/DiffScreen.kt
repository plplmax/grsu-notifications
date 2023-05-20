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
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiffScreen() {
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
            pageCount = 3,
            contentPadding = PaddingValues(14.dp),
            pageSpacing = 4.dp,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ScheduleCard()
                ScheduleCard()
                ScheduleCard()
                ScheduleCard()
                ScheduleCard()
                ScheduleCard()
                ScheduleCard()
            }
        }
    }
}

@Composable
fun ScheduleCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier.weight(0.2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DiffText(text = "11:40")
                DiffText(text = "13:00")
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                DiffText(text = "- Основы декларативного программирования")
                DiffText(text = "+ Администрирование информационных систем")
                DiffText(
                    text = "+ Дейцева Анна Геннадьевна",
                    style = MaterialTheme.typography.titleSmall
                )
                DiffText(text = "Ожешко, 22, 220", style = MaterialTheme.typography.titleSmall)
            }
            Column(modifier = Modifier.weight(0.3f)) {
                DiffText(
                    text = "- Экзамен (в письменной форме)",
                    style = MaterialTheme.typography.titleSmall
                )
                DiffText(
                    text = "+ Экзамен (в устной форме)",
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
fun DiffScreenPreview() {
    GrsuNotificationsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DiffScreen()
        }
    }
}
