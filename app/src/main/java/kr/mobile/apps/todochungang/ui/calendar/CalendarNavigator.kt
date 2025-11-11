package kr.mobile.apps.todochungang.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Modifier.CalendarNavigator(
    initialMonth: YearMonth = YearMonth.now(),
    eventsForMonth: (YearMonth) -> List<CalendarEvent> = { emptyList() }
) {
    var ymString by rememberSaveable { mutableStateOf(initialMonth.toString()) }
    val month = remember(ymString) { YearMonth.parse(ymString) }

    Column(modifier = this.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { ymString = month.minusYears(1).toString() }) {
                    Icon(Icons.Filled.KeyboardDoubleArrowLeft, contentDescription = "Previous year")
                }
                IconButton(onClick = { ymString = month.minusMonths(1).toString() }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
                }
            }

            Text(
                text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } +
                        " ${month.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { ymString = month.plusMonths(1).toString() }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
                }
                IconButton(onClick = { ymString = month.plusYears(1).toString() }) {
                    Icon(Icons.Filled.KeyboardDoubleArrowRight, contentDescription = "Next year")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { ymString = YearMonth.now().toString() }) {
            Text("Today")
        }

        Spacer(Modifier.height(8.dp))

        CalendarScreen(
            month = month,
            events = eventsForMonth(month),
            modifier = Modifier.fillMaxWidth()
        )
    }
}




fun sampleEventsForMonth(month: YearMonth): List<CalendarEvent> {
    //val day = LocalDate.of(month.year, month.month, minOf(11, month.lengthOfMonth()))
    return listOf()
}
