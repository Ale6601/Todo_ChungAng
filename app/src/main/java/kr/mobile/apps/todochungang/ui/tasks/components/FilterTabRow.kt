package kr.mobile.apps.todochungang.ui.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.mobile.apps.todochungang.ui.tasks.TaskFilter

@Composable
fun FilterTabRow(
    currentFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    allCount: Int,
    activeCount: Int,
    completedCount: Int
) {
    val tabs = TaskFilter.entries.toTypedArray()
    val pillShape = RoundedCornerShape(999.dp)
    val containerColor = Color(0xFFF3F4F6)   

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = containerColor,
        shape = pillShape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.height(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { filter ->
                val count = when (filter) {
                    TaskFilter.ALL -> allCount
                    TaskFilter.ACTIVE -> activeCount
                    TaskFilter.COMPLETED -> completedCount
                }

                val selected = currentFilter == filter

                val tabName = filter.name.lowercase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(2.dp) 
                        .background(
                            color = if (selected) Color.White else Color.Transparent,
                            shape = pillShape
                        )
                        .clickable { onFilterSelected(filter) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$tabName ($count)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
