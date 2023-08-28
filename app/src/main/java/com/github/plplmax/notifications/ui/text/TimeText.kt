package com.github.plplmax.notifications.ui.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle

@Composable
fun TimeText(time: LocalTime, modifier: Modifier = Modifier) {
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    val formatter = DateTimeFormatterBuilder().appendLocalized(null, FormatStyle.SHORT)
        .toFormatter(locale)
    Text(text = formatter.format(time), modifier = modifier)
}
