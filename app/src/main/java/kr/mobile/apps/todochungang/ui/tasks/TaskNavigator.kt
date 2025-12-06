package kr.mobile.apps.todochungang.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskNavigator(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    // Saturday, December 6, 2025
    val dayFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
    }

    // ⬇️ CalendarNavigator 와 똑같이 padding(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽 그룹 (CalendarNavigator 의 <<, < 위치와 동일)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPreviousDay) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Previous day"
                    )
                }
            }

            // 가운데 날짜 (CalendarNavigator 의 "December 2025"와 스타일 동일)
            Text(
                text = selectedDate.format(dayFormatter),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            // 오른쪽 그룹 (CalendarNavigator 의 >, >> 위치와 동일 구조)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNextDay) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Next day"
                    )
                }
            }
        }

        // CalendarNavigator 도 헤더 아래 8dp 줌
        Spacer(Modifier.height(8.dp))
    }
}
