package kr.mobile.apps.todochungang.ui.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kr.mobile.apps.todochungang.data.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DateRangeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

private fun formatTaskDateRange(startDate: LocalDate?, endDate: LocalDate?): String? {
    if (startDate == null) return null
    val startPart = startDate.format(DateRangeFormatter)

    if (endDate == null || endDate == startDate) {
        return startPart
    }

    val endPart = endDate.format(DateRangeFormatter)
    return "$startPart ~ $endPart"
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,
) {
    LazyColumn(modifier = Modifier.Companion.fillMaxWidth()) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onToggleComplete = onToggleComplete,
                onDeleteTask = onDeleteTask,
                onTaskClick = onTaskClick
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .background(Color.Companion.White)
            .clickable { onTaskClick(task) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        val checkboxIcon =
            if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked
        val checkboxColor =
            if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Companion.Gray

        IconButton(
            onClick = { onToggleComplete(task) },
            modifier = Modifier.Companion.size(48.dp)
        ) {
            Icon(
                imageVector = checkboxIcon,
                contentDescription = "Task 완료 상태",
                tint = checkboxColor,
                modifier = Modifier.Companion.size(24.dp)
            )
        }

        Spacer(Modifier.Companion.width(8.dp))

        Column(modifier = Modifier.Companion.weight(1f)) {
            Text(
                text = task.title.replace('\n', ' '),
                modifier = Modifier.Companion.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Companion.Ellipsis,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.Companion.LineThrough else null,
                    color = if (task.isCompleted) Color.Companion.Gray else Color.Companion.Black
                )
            )

            val dateRangeText = formatTaskDateRange(task.startDate, task.endDate)
            if (dateRangeText != null) {
                Spacer(modifier = Modifier.Companion.height(2.dp))
                Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                    Icon(
                        Icons.Filled.Event,
                        contentDescription = "기한 범위",
                        modifier = Modifier.Companion.size(16.dp),
                        tint = Color.Companion.DarkGray
                    )
                    Spacer(modifier = Modifier.Companion.width(4.dp))

                    Text(
                        text = dateRangeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Companion.DarkGray
                    )
                }
            }
        }

        IconButton(onClick = { onDeleteTask(task) }) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Task 삭제",
                tint = Color.Companion.Red
            )
        }
    }
}