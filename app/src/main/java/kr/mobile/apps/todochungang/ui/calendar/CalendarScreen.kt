@file:OptIn(ExperimentalFoundationApi::class)
package kr.mobile.apps.todochungang.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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

@Composable
fun CalendarScreen(
    month: YearMonth,
    events: List<CalendarEvent>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        MonthHeader(month)
        Spacer(Modifier.height(8.dp))
        WeekdayRow()
        Spacer(Modifier.height(4.dp))

        val days = remember(month) { daysForMonthGrid(month) }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false
        ) {
            items(days) { day ->
                DayCell(
                    day = day,
                    inMonth = day.month == month.month,
                    eventsToday = eventsForDay(day, events),
                )
            }
        }
    }
}

@Composable
private fun MonthHeader(month: YearMonth) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } +
                    " ${month.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        // prev/next buttons can go here later
    }
}

@Composable
private fun WeekdayRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DayOfWeek.entries.forEach { dow ->
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
) {
    val borderColor = Color(0xFFE9E9EF)
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(6.dp)
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (inMonth) Color.Unspecified else Color(0xFF9AA0A6)
        )
        Spacer(Modifier.height(4.dp))
        val maxBars = 3
        eventsToday.take(maxBars).forEach { slice ->
            EventBar(slice)
            Spacer(Modifier.height(4.dp))
        }
        val remaining = eventsToday.size - maxBars
        if (remaining > 0) {
            Text("+$remaining more", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun EventBar(slice: SpanSlice) {
    val r = 8.dp
    val shape = when (slice.position) {
        SlicePos.SINGLE -> RoundedCornerShape(r)
        SlicePos.START -> RoundedCornerShape(topStart = r, bottomStart = r)
        SlicePos.MIDDLE -> RoundedCornerShape(0.dp)
        SlicePos.END -> RoundedCornerShape(topEnd = r, bottomEnd = r)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .clip(shape)
            .background(slice.color.copy(alpha = 0.9f))
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = slice.title,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* ---------- Helpers ---------- */

private fun daysForMonthGrid(month: YearMonth): List<LocalDate> {
    val firstOfMonth = month.atDay(1)
    // start week on Sunday; change to Monday by shifting logic here
    val firstCell = firstOfMonth.minusDays(((firstOfMonth.dayOfWeek.value % 7)).toLong())
    return (0 until 42).map { firstCell.plusDays(it.toLong()) } // 6 weeks
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
    val month = YearMonth.of(2025, 11)
    val sample = listOf(
        CalendarEvent("1", "Morning workout",
            LocalDate.of(2025, 11, 11), LocalDate.of(2025, 11, 11), Color(0xFFE74C3C)),
        CalendarEvent("2", "Team meeting",
            LocalDate.of(2025, 11, 11), LocalDate.of(2025, 11, 14), Color(0xFFE74C3C)),
        CalendarEvent("3", "Review project documentation",
            LocalDate.of(2025, 11, 11), LocalDate.of(2025, 11, 11), Color(0xFF5DADE2)),
    )
    MaterialTheme { Surface { CalendarScreen(month, sample, Modifier.fillMaxSize()) } }
}
