package kr.mobile.apps.todochungang.ui.tasks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
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
    onTaskClick: (Task) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onToggleComplete = onToggleComplete,
                onDeleteTask = onDeleteTask,
                onTaskClick = onTaskClick
            )
            Spacer(modifier = Modifier.height(8.dp))
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable { onTaskClick(task) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFDDDDDD))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val checkboxIcon =
                if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked

            val checkboxColor =
                if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray

            IconButton(
                onClick = { onToggleComplete(task) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = checkboxIcon,
                    contentDescription = "Toggle Task Complete",
                    tint = checkboxColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title.replace('\n', ' '),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) Color.Gray else Color.Black
                    )
                )

                val dateRange = formatTaskDateRange(task.startDate, task.endDate)
                if (dateRange != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = "Task Date Range",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = dateRange,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            IconButton(
                onClick = { onDeleteTask(task) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Task",
                    tint = Color(0xFFD9534F),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
