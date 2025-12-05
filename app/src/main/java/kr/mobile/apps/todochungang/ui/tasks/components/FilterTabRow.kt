package kr.mobile.apps.todochungang.ui.tasks.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val selectedIndex = tabs.indexOf(currentFilter)

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.Companion.fillMaxWidth(),
        divider = { HorizontalDivider(color = Color.Companion.Transparent) }
    ) {
        tabs.forEachIndexed { index, filter ->
            val count = when (filter) {
                TaskFilter.ALL -> allCount
                TaskFilter.ACTIVE -> activeCount
                TaskFilter.COMPLETED -> completedCount
            }
            Tab(
                selected = selectedIndex == index,
                onClick = { onFilterSelected(filter) },
                text = {
                    val tabName = filter.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Text(text = "$tabName ($count)")
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(modifier = Modifier.Companion.fillMaxWidth())
}