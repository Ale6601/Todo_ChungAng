package kr.mobile.apps.todochungang.ui.tasks.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kr.mobile.apps.todochungang.ui.tasks.pickers.TaskTimePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (
        title: String,
        details: String,
        startTime: LocalTime?,
        endTime: LocalTime?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ) -> Unit,
    initialDate: LocalDate
) {
    var taskTitle by remember { mutableStateOf("") }
    var taskDetails by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var pickingEndDate by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(initialDate) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var showTimePicker by remember { mutableStateOf(false) }
    var pickingEndTime by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    val isAddButtonEnabled = taskTitle.isNotBlank()

    // ---------- 날짜 선택 다이얼로그 ----------
    if (showDatePicker) {
        val initialDateForPicker = if (pickingEndDate) endDate ?: startDate else startDate
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateForPicker
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        if (pickingEndDate) {
                            endDate = newDate
                        } else {
                            startDate = newDate
                            if (endDate == null || newDate.isAfter(endDate)) {
                                endDate = newDate
                            }
                        }
                    }
                    showDatePicker = false
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) { DatePicker(state = dateState) }
    }

    // ---------- 시간 선택 다이얼로그 ----------
    if (showTimePicker) {
        TaskTimePickerDialog(
            initialTime = if (pickingEndTime) endTime else startTime,
            onTimeSelected = { newTime ->
                if (pickingEndTime) endTime = newTime else startTime = newTime
            },
            onDismiss = { showTimePicker = false }
        )
    }

    // ---------- 메인 AddTask Dialog ----------
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "새 할 일 추가",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("할 일 제목") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = taskDetails,
                    onValueChange = { taskDetails = it },
                    label = { Text("세부 정보 (선택 사항)") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 날짜
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { pickingEndDate = false; showDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Filled.Event,
                                contentDescription = "시작일 변경",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                startDate.format(
                                    DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("~")
                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = { pickingEndDate = true; showDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Filled.Event,
                                contentDescription = "마감일 변경",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                endDate?.format(
                                    DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)
                                ) ?: "마감일"
                            )
                        }
                    }

                    // 시간
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { pickingEndTime = false; showTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            val timeText = startTime?.format(
                                DateTimeFormatter.ofPattern("h:mm a")
                            ) ?: "시작 시간"
                            Icon(
                                Icons.Filled.Schedule,
                                contentDescription = "시작 시간 설정",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(timeText)
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("~")
                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = { pickingEndTime = true; showTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            val timeText = endTime?.format(
                                DateTimeFormatter.ofPattern("h:mm a")
                            ) ?: "마감 시간"
                            Icon(
                                Icons.Filled.Schedule,
                                contentDescription = "마감 시간 설정",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(timeText)
                        }

                        if (startTime != null || endTime != null) {
                            IconButton(onClick = { startTime = null; endTime = null }) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "시간 지우기",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("취소") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onAddTask(
                                taskTitle,
                                taskDetails,
                                startTime,
                                endTime,
                                startDate,
                                endDate
                            )
                        },
                        enabled = isAddButtonEnabled
                    ) {
                        Text("추가")
                    }
                }
            }
        }
    }
}
