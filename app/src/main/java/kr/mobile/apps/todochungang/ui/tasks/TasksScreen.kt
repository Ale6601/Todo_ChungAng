package kr.mobile.apps.todochungang.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kr.mobile.apps.todochungang.data.model.Task
import kr.mobile.apps.todochungang.ui.tasks.components.AddTaskFab
import kr.mobile.apps.todochungang.ui.tasks.components.FilterTabRow
import kr.mobile.apps.todochungang.ui.tasks.components.TaskList
import kr.mobile.apps.todochungang.ui.tasks.dialogs.AddTaskDialog
import kr.mobile.apps.todochungang.ui.tasks.dialogs.TaskDetailDialog
import java.time.LocalDate


private val BackgroundColor = Color(0xFFFFFFFF)

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier, viewModel: TasksViewModel = viewModel()
) {

    val isAddingTask by viewModel.isAddingTask.collectAsState(initial = false)
    val taskTitleLoading by viewModel.taskTitleLoading.collectAsState(initial = "")
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()


    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }


    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }


    val tasksForSelectedDate = remember(selectedDate, filteredTasks) {
        filteredTasks.filter { task ->
            val start = task.startDate
            val end = task.endDate ?: start
            if (start == null) {

                true
            } else {
                val effectiveEnd = end ?: start
                !selectedDate.isBefore(start) && !selectedDate.isAfter(effectiveEnd)
            }
        }
    }


    val allCountForDate = tasksForSelectedDate.size
    val activeCountForDate = tasksForSelectedDate.count { !it.isCompleted }
    val completedCountForDate = tasksForSelectedDate.count { it.isCompleted }

    Scaffold(
        containerColor = BackgroundColor,
        floatingActionButton = {

            Box(modifier = Modifier.padding(bottom = 50.dp)) {
                AddTaskFab(onClick = { showAddTaskDialog = true })
            }
        },
    ) { _ ->
        Column(
            modifier = modifier.fillMaxSize()
        ) {

            TaskNavigator(
                selectedDate = selectedDate,
                onPreviousDay = { selectedDate = selectedDate.minusDays(1) },
                onNextDay = { selectedDate = selectedDate.plusDays(1) })


            FilterTabRow(
                currentFilter = currentFilter,
                onFilterSelected = viewModel::setFilter,
                allCount = allCountForDate,
                activeCount = activeCountForDate,
                completedCount = completedCountForDate
            )


            if (currentFilter == TaskFilter.COMPLETED && tasksForSelectedDate.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.deleteAllCompletedTasks() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("모든 완료 항목 삭제")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }


            if (isAddingTask && taskTitleLoading.isNotBlank()) {
                LoadingFeedbackBox(taskTitle = taskTitleLoading)
                Spacer(modifier = Modifier.height(16.dp))
            }


            Box(modifier = Modifier.weight(1f)) {
                if (tasksForSelectedDate.isEmpty() && !isAddingTask) {
                    EmptyStateMessage()
                } else {
                    TaskList(
                        tasks = tasksForSelectedDate,
                        onToggleComplete = viewModel::toggleTaskCompletion,
                        onDeleteTask = viewModel::deleteTask,
                        onTaskClick = { task -> selectedTask = task })
                }
            }
        }
    }


    selectedTask?.let { task ->
        TaskDetailDialog(
            task = task,
            viewModel = viewModel,
            onDismiss = { selectedTask = null },
            onDeleteTask = {
                viewModel.deleteTask(task)
                selectedTask = null
            },
            onToggleCompleted = { isChecked ->
                viewModel.toggleTaskCompletion(task.copy(isCompleted = isChecked))
            })
    }


    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, details, startTime, endTime, startDate, endDate ->
                viewModel.addTask(title, details, startTime, endTime, startDate, endDate)
                showAddTaskDialog = false
            },

            initialDate = selectedDate
        )
    }
}
