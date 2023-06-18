package com.github.plplmax.notifications.ui.welcome

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme

@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .verticalScroll(rememberScrollState())
            .padding(start = 14.dp, end = 14.dp, top = 14.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.welcome),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .weight(1.5f)
                .width(IntrinsicSize.Min)
                .align(Alignment.CenterHorizontally)
                .padding(top = 64.dp),
        ) {
            Text(
                text = stringResource(R.string.grsu_notifications),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = stringResource(R.string.stay_up_to_date_on_your_schedule),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(330.dp)
                    .padding(bottom = 32.dp),
            )
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.get_started))
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenPreview() {
    GrsuNotificationsTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            WelcomeScreen()
        }
    }
}

