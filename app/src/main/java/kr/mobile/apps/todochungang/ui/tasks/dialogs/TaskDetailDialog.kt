package kr.mobile.apps.todochungang.ui.tasks.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import kr.mobile.apps.todochungang.data.model.Task
import kr.mobile.apps.todochungang.ui.tasks.TasksViewModel
import kr.mobile.apps.todochungang.ui.tasks.pickers.TaskDatePickerDialog
import kr.mobile.apps.todochungang.ui.tasks.pickers.TaskTimePickerDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TaskDetailDialog(
    task: Task,
    viewModel: TasksViewModel,
    onDismiss: () -> Unit,
    onDeleteTask: () -> Unit,
) {
    var currentTitle by remember { mutableStateOf(task.title) }
    var currentDetails by remember { mutableStateOf(task.details) }

    var currentStartDate by remember { mutableStateOf(task.startDate) }
    var currentEndDate by remember { mutableStateOf(task.endDate ?: task.startDate) }
    var currentStartTime by remember { mutableStateOf(task.startTime) }
    var currentEndTime by remember { mutableStateOf(task.endTime) }

    var isDatePickerShowing by remember { mutableStateOf(false) }
    var pickingEndDate by remember { mutableStateOf(false) }

    var isTimePickerShowing by remember { mutableStateOf(false) }
    var pickingEndTime by remember { mutableStateOf(false) }

    var isDetailsExpanded by remember { mutableStateOf(task.details.isNotBlank()) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }


    if (isDatePickerShowing) {
        val initialDate = if (pickingEndDate) {
            currentEndDate ?: currentStartDate ?: LocalDate.now()
        } else {
            currentStartDate ?: currentEndDate ?: LocalDate.now()
        }

        TaskDatePickerDialog(initialDate = initialDate, onDateSelected = { newDate ->
            if (pickingEndDate) {
                currentEndDate = newDate

                if (currentStartDate != null && newDate.isBefore(currentStartDate)) {
                    currentStartDate = newDate
                }
            } else {
                currentStartDate = newDate

                if (currentEndDate == null || newDate.isAfter(currentEndDate)) {
                    currentEndDate = newDate
                }
            }
        }, onDismiss = { isDatePickerShowing = false })
    }


    if (isTimePickerShowing) {
        TaskTimePickerDialog(
            initialTime = if (pickingEndTime) currentEndTime else currentStartTime,
            onTimeSelected = { newTime ->
                if (pickingEndTime) {
                    currentEndTime = newTime
                } else {
                    currentStartTime = newTime
                }
            },
            onDismiss = { isTimePickerShowing = false })
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.dp, Color(0xFFCCCCCC))
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentTitle,
                        onValueChange = { currentTitle = it },
                        label = { Text("Task Title") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color(0xFF9CA3AF),
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color(0xFFDDDDDD),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    IconButton(onClick = onDeleteTask) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))


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
                            onClick = {
                                pickingEndDate = false
                                isDatePickerShowing = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = currentStartDate?.format(dateFormatter) ?: "Set Start Date"
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("~")
                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = {
                                pickingEndDate = true
                                isDatePickerShowing = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = currentEndDate?.format(dateFormatter) ?: "Set End Date"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            pickingEndTime = false
                            isTimePickerShowing = true
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        val startTimeText = currentStartTime?.format(
                            DateTimeFormatter.ofPattern("a h:mm")
                        ) ?: "Start Time"
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = "Set Start Time",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(startTimeText)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text("~")
                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            pickingEndTime = true
                            isTimePickerShowing = true
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        val endTimeText = currentEndTime?.format(
                            DateTimeFormatter.ofPattern("a h:mm")
                        ) ?: "End Time"
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = "Set End Time",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(endTimeText)
                    }

                    if (currentStartTime != null || currentEndTime != null) {
                        IconButton(onClick = {
                            currentStartTime = null
                            currentEndTime = null
                        }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Clear Time",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                if (isDetailsExpanded) {
                    OutlinedTextField(
                        value = currentDetails,
                        onValueChange = { currentDetails = it },
                        label = if (currentDetails.isEmpty()) {
                            { Text("Task Description") }
                        } else null,
                        placeholder = if (currentDetails.isEmpty()) {
                            { Text("Enter Task Description", color = Color.Gray) }
                        } else null,
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Task Details Icon"
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color(0xFF9CA3AF),
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color(0xFFDDDDDD),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { isDetailsExpanded = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Black
                            )
                        ) { Text("Hide") }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isDetailsExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.Notes,
                            contentDescription = "Task Details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = if (task.details.isNotBlank()) task.details.take(20) + if (task.details.length > 20) "..." else ""
                            else "Add Task Description",
                            color = if (task.details.isNotBlank()) Color.DarkGray
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }


                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss, colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val finalEndDate = currentEndDate ?: currentStartDate
                            viewModel.updateTaskDetails(
                                task.id,
                                currentDetails,
                                currentStartDate,
                                finalEndDate,
                                currentStartTime,
                                currentEndTime
                            )
                            onDismiss()
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
