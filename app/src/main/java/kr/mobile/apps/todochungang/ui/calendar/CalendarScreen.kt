@file:OptIn(ExperimentalFoundationApi::class)

package kr.mobile.apps.todochungang.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class CalendarEvent(
    val id: String,
    val title: String,
    val start: LocalDate,
    val end: LocalDate,
    val color: Color = Color(0xFFE74C3C)
)

enum class WeekStart {
    MONDAY,
    SUNDAY
}

@Composable
fun CalendarScreen(
    month: YearMonth,
    events: List<CalendarEvent>,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now(),
    weekStart: WeekStart = WeekStart.MONDAY,
    backgroundColor: Color = Color.White
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        WeekdayRow(weekStart = weekStart)
        Spacer(Modifier.height(4.dp))

        val days = remember(month, weekStart) { daysForMonthGrid(month, weekStart) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),   
            verticalArrangement = Arrangement.spacedBy(0.dp),     
            userScrollEnabled = false
        ) {
            items(days) { day ->
                DayCell(
                    day = day,
                    inMonth = day.month == month.month,
                    eventsToday = eventsForDay(day, events),
                    isToday = day == today && day.month == month.month && day.year == today.year
                )
            }
        }
    }
}

@Composable
private fun WeekdayRow(weekStart: WeekStart) {
    val weekOrder = when (weekStart) {
        WeekStart.MONDAY -> listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
        WeekStart.SUNDAY -> listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        weekOrder.forEach { dow ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

private data class SpanSlice(val title: String, val position: SlicePos, val color: Color)
private enum class SlicePos { START, MIDDLE, END, SINGLE }

@Composable
private fun DayCell(
    day: LocalDate,
    inMonth: Boolean,
    eventsToday: List<SpanSlice>,
    isToday: Boolean
) {
    val maxBars = 1
    val remaining = (eventsToday.size - maxBars).coerceAtLeast(0)

    
    val displayEvents = remember(eventsToday) {
        eventsToday.sortedBy { slice ->
            if (slice.position == SlicePos.SINGLE) 1 else 0
        }
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(vertical = 6.dp)   
    ) {
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,   
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = when {
                    !inMonth -> Color(0xFF9AA0A6)
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> Color.Unspecified
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )

            
            if (remaining > 0) {
                Text(
                    text = "+$remaining",
                    modifier = Modifier.padding(start = 4.dp),   
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }


        Spacer(Modifier.height(4.dp))

        
        displayEvents.take(maxBars).forEach { slice ->
            EventBar(slice)
        }
    }
}

@Composable
private fun EventBar(slice: SpanSlice) {

    val r = 2.dp   
    val endTrim = 6.dp  

    val shape = when (slice.position) {
        SlicePos.SINGLE -> RoundedCornerShape(r)
        SlicePos.START -> RoundedCornerShape(topStart = r, bottomStart = r)
        SlicePos.MIDDLE -> RoundedCornerShape(0.dp)
        SlicePos.END -> RoundedCornerShape(topEnd = r, bottomEnd = r)
    }

    val baseColor = slice.color.copy(alpha = 0.18f)
    val edgeColor = slice.color

    
    val adjustedModifier = when (slice.position) {
        SlicePos.END -> Modifier
            .fillMaxWidth()
            .padding(end = endTrim)  
        else -> Modifier.fillMaxWidth()
    }

    Box(
        modifier = adjustedModifier
            .height(16.dp)
            .clip(shape)
            .background(baseColor),
        contentAlignment = Alignment.CenterStart
    ) {
        
        if (slice.position == SlicePos.START || slice.position == SlicePos.SINGLE) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .background(edgeColor)
            )
        }

        Text(
            text = slice.title,
            modifier = Modifier.padding(start = 6.dp, end = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = edgeColor.copy(alpha = 0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}




/* ---------- Helpers ---------- */

private fun daysForMonthGrid(
    month: YearMonth,
    weekStart: WeekStart
): List<LocalDate> {
    val firstOfMonth = month.atDay(1)

    val firstDayOfWeek = when (weekStart) {
        WeekStart.MONDAY -> DayOfWeek.MONDAY
        WeekStart.SUNDAY -> DayOfWeek.SUNDAY
    }

    val dayOfWeekValue = firstOfMonth.dayOfWeek.value   
    val startValue = firstDayOfWeek.value               

    val diff = (7 + (dayOfWeekValue - startValue)) % 7
    val firstCell = firstOfMonth.minusDays(diff.toLong())

    return (0 until 42).map { firstCell.plusDays(it.toLong()) } 
}

private fun eventsForDay(day: LocalDate, events: List<CalendarEvent>): List<SpanSlice> {
    val candidates = events.filter { !day.isBefore(it.start) && !day.isAfter(it.end) }
    return candidates.map { e ->
        val pos = when {
            day == e.start && day == e.end -> SlicePos.SINGLE
            day == e.start -> SlicePos.START
            day == e.end -> SlicePos.END
            else -> SlicePos.MIDDLE
        }
        val title = if (pos == SlicePos.START || pos == SlicePos.SINGLE) e.title else ""
        SpanSlice(title, pos, e.color)
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 500)
@Composable
private fun CalendarScreenPreview() {
    val month = YearMonth.of(2025, 12)
    val sample = listOf(
        CalendarEvent(
            "1",
            "123",
            LocalDate.of(2025, 12, 9),
            LocalDate.of(2025, 12, 10),
            Color(0xFFE74C3C)
        ),
        CalendarEvent(
            "2",
            "하루짜리",
            LocalDate.of(2025, 12, 9),
            LocalDate.of(2025, 12, 9),
            Color(0xFF5DADE2)
        ),
        CalendarEvent(
            "3",
            "또 다른 작업",
            LocalDate.of(2025, 12, 9),
            LocalDate.of(2025, 12, 11),
            Color(0xFF8E44AD)
        )
    )
    MaterialTheme {
        Surface {
            CalendarScreen(
                month = month,
                events = sample,
                weekStart = WeekStart.MONDAY,
                backgroundColor = Color(0xFFE3F2FD)
            )
        }
    }
}
