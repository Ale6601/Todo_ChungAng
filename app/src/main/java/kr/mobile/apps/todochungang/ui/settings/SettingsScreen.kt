package kr.mobile.apps.todochungang.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.mobile.apps.todochungang.ui.calendar.WeekStart

@Composable
fun SettingsScreen(
    weekStart: WeekStart,
    onWeekStartChange: (WeekStart) -> Unit,
    backgroundColor: Color,
    onBackgroundColorChange: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        CalendarFormatCard(
            weekStart = weekStart, onWeekStartChange = onWeekStartChange
        )

        BackgroundColorCard(
            selectedColor = backgroundColor, onColorSelected = onBackgroundColorChange
        )
    }
}

@Composable
private fun CalendarFormatCard(
    weekStart: WeekStart, onWeekStartChange: (WeekStart) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Calendar format",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Choose whether weeks start on Monday or Sunday.",
                style = MaterialTheme.typography.bodySmall
            )

            val options = listOf(WeekStart.MONDAY, WeekStart.SUNDAY)

            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = weekStart == item,
                        onClick = { onWeekStartChange(item) },
                        shape = SegmentedButtonDefaults.itemShape(index, options.size)
                    ) {
                        Text(
                            text = when (item) {
                                WeekStart.MONDAY -> "Monday"
                                WeekStart.SUNDAY -> "Sunday"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BackgroundColorCard(
    selectedColor: Color, onColorSelected: (Color) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Calendar background color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Pick a background color for your calendar.",
                style = MaterialTheme.typography.bodySmall
            )

            val colors = listOf(
                Color(0xFFFFFFFF), Color(0xFFFFF8E1), Color(0xFFE3F2FD), Color(0xFFE8F5E9)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = color, shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onColorSelected(color) }
                            .borderIfSelected(color == selectedColor),
                    )
                }
            }
        }
    }
}


@Composable
private fun Modifier.borderIfSelected(selected: Boolean): Modifier {
    return if (selected) {
        this.then(
            Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
        )
    } else {
        this
    }
}
