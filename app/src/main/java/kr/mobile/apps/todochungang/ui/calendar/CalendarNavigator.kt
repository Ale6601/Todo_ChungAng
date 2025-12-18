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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarNavigator(
    modifier: Modifier = Modifier,
    calendarViewModel: CalendarViewModel = viewModel(),
    weekStart: WeekStart,
    backgroundColor: Color
) {
    val uiState by calendarViewModel.uiState.collectAsState()

    val month: YearMonth = uiState.currentMonth
    val events = uiState.events
    val today = LocalDate.now()

    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { calendarViewModel.changeMonth(month.minusYears(1)) }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                        contentDescription = "Previous year"
                    )
                }
                IconButton(onClick = { calendarViewModel.changeMonth(month.minusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Previous month"
                    )
                }
            }

            
            Text(
                text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    } + " ${month.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = {
                        val thisMonth = YearMonth.from(today)
                        calendarViewModel.changeMonth(thisMonth)
                    }
                ) {
                    Text("Today")
                }
                IconButton(onClick = { calendarViewModel.changeMonth(month.plusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Next month"
                    )
                }
                IconButton(onClick = { calendarViewModel.changeMonth(month.plusYears(1)) }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                        contentDescription = "Next year"
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        CalendarScreen(
            month = month,
            events = events,
            modifier = Modifier.fillMaxWidth(),
            today = today,
            weekStart = weekStart,
            backgroundColor = backgroundColor
        )
    }
}
