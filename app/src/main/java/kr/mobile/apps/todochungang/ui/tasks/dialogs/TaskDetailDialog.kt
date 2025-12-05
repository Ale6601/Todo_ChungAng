package kr.mobile.apps.todochungang.ui.tasks.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kr.mobile.apps.todochungang.data.model.Task
import kr.mobile.apps.todochungang.ui.tasks.TasksViewModel
import kr.mobile.apps.todochungang.ui.tasks.pickers.TaskDatePickerDialog
import kr.mobile.apps.todochungang.ui.tasks.pickers.TaskTimePickerDialog

@Composable
fun TaskDetailDialog(
    task: Task,
    viewModel: TasksViewModel,
    onDismiss: () -> Unit,
    onDeleteTask: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    var currentTitle by remember { mutableStateOf(task.title) }
    var currentDetails by remember { mutableStateOf(task.details) }

    var currentStartDate by remember { mutableStateOf(task.startDate) }
    var currentEndDate by remember { mutableStateOf(task.endDate) }
    var currentStartTime by remember { mutableStateOf(task.startTime) }
    var currentEndTime by remember { mutableStateOf(task.endTime) }

    var isDatePickerShowing by remember { mutableStateOf(false) }
    var isTimePickerShowing by remember { mutableStateOf(false) }
    var pickingEndTime by remember { mutableStateOf(false) }
    var isDetailsExpanded by remember { mutableStateOf(task.details.isNotBlank()) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy. M . d") }

    if (isDatePickerShowing) {
        TaskDatePickerDialog(
            initialDate = currentStartDate ?: LocalDate.now(),
            onDateSelected = { newDate -> currentStartDate = newDate },
            onDismiss = { isDatePickerShowing = false }
        )
    }

    if (isTimePickerShowing) {
        TaskTimePickerDialog(
            initialTime = if (pickingEndTime) currentEndTime else currentStartTime,
            onTimeSelected = { newTime ->
                if (pickingEndTime) currentEndTime = newTime else currentStartTime = newTime
            },
            onDismiss = { isTimePickerShowing = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // 제목 + 삭제 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentTitle,
                        onValueChange = { currentTitle = it },
                        label = { Text("할 일 제목") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                        )
                    )
                    IconButton(onClick = onDeleteTask) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "할 일 삭제",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // 완료 체크
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .offset(x = (-8).dp)
                        .padding(bottom = 12.dp)
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = onToggleCompleted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (task.isCompleted) "완료됨" else "미완료",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.isCompleted) Color.Gray else Color.Black
                    )
                }

                // 날짜
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { isDatePickerShowing = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = currentStartDate?.format(dateFormatter) ?: "시작일 설정")
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("~")
                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = { isDatePickerShowing = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = currentEndDate?.format(dateFormatter) ?: "마감일 설정")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 시간
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { pickingEndTime = false; isTimePickerShowing = true }) {
                        val startTimeText = currentStartTime?.format(
                            DateTimeFormatter.ofPattern("a h:mm")
                        ) ?: "시작 시간"
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = "시작 시간 설정",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(startTimeText)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text("~")
                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = { pickingEndTime = true; isTimePickerShowing = true }) {
                        val endTimeText = currentEndTime?.format(
                            DateTimeFormatter.ofPattern("a h:mm")
                        ) ?: "마감 시간"
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = "마감 시간 설정",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(endTimeText)
                    }

                    if (currentStartTime != null || currentEndTime != null) {
                        IconButton(onClick = { currentStartTime = null; currentEndTime = null }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "시간 지우기",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 세부 정보
                if (isDetailsExpanded) {
                    OutlinedTextField(
                        value = currentDetails,
                        onValueChange = { currentDetails = it },
                        label = if (currentDetails.isEmpty()) {
                            { Text("세부 정보") }
                        } else null,
                        placeholder = if (currentDetails.isEmpty()) {
                            { Text("세부 정보를 입력하세요...", color = Color.Gray) }
                        } else null,
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "세부 정보 아이콘"
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isDetailsExpanded = false }) { Text("숨기기") }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isDetailsExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Notes,
                            contentDescription = "세부 정보 아이콘",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = if (task.details.isNotBlank())
                                task.details.take(20) + if (task.details.length > 20) "..." else ""
                            else "세부 정보 추가",
                            color = if (task.details.isNotBlank())
                                Color.DarkGray
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 저장 / 닫기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        viewModel.updateTaskDetails(
                            task.id,
                            currentDetails,
                            currentStartDate,
                            currentEndDate,
                            currentStartTime,
                            currentEndTime
                        )
                        onDismiss()
                    }) {
                        Text("저장")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDismiss) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}
