package com.github.plplmax.notifications.ui.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.core.os.ConfigurationCompat
import com.github.plplmax.notifications.R
import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun DateText(date: LocalDate, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0]!!
    val formatter = getLocalizedDateFormatter(date, locale)
    val text = when (date) {
        today -> stringResource(id = R.string.today)
        yesterday -> stringResource(id = R.string.yesterday)
        else -> formatter.format(date)
    }
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}

@Composable
private fun getLocalizedDateFormatter(date: LocalDate, locale: Locale): DateTimeFormatter {
    val today = LocalDate.now()
    val pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.MEDIUM,
        null,
        IsoChronology.INSTANCE,
        locale
    )
    val removeYearRegex = remember(pattern) {
        // https://stackoverflow.com/a/12490796/17650498
        Regex(if (pattern.contains("de")) "[^Mm]*[Yy]+[^Mm]*" else "[^DdMm]*[Yy]+[^DdMm]*")
    }
    val showYear = date.year != today.year
    return if (showYear) {
        DateTimeFormatter.ofPattern(pattern, locale)
    } else {
        DateTimeFormatter.ofPattern(pattern.replace(removeYearRegex, ""), locale)
    }
}
