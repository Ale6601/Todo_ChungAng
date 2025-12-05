package kr.mobile.apps.todochungang.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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

private val LightGrayBackground = Color(0xFFF3F3F3)

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    val isAddingTask by viewModel.isAddingTask.collectAsState(initial = false)
    val taskTitleLoading by viewModel.taskTitleLoading.collectAsState(initial = "")
    val tasks by viewModel.filteredTasks.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val allCount = viewModel.allCount
    val activeCount = viewModel.activeCount
    val completedCount = viewModel.completedCount

    Scaffold(
        modifier = modifier,
        containerColor = LightGrayBackground,
        floatingActionButton = {
            AddTaskFab(onClick = { showAddTaskDialog = true })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            FilterTabRow(
                currentFilter = currentFilter,
                onFilterSelected = viewModel::setFilter,
                allCount = allCount,
                activeCount = activeCount,
                completedCount = completedCount
            )

            if (currentFilter == TaskFilter.COMPLETED && tasks.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.deleteAllCompletedTasks() },
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            contentColor = androidx.compose.ui.graphics.Color.Red
                        )
                    ) {
                        androidx.compose.material3.Text("모든 완료 항목 삭제")
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
                if (tasks.isEmpty() && !isAddingTask) {
                    EmptyStateMessage()
                } else {
                    TaskList(
                        tasks = tasks,
                        onToggleComplete = viewModel::toggleTaskCompletion,
                        onDeleteTask = viewModel::deleteTask,
                        onTaskClick = { task -> selectedTask = task }
                    )
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
            }
        )
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, details, startTime, endTime, startDate, endDate ->
                viewModel.addTask(title, details, startTime, endTime, startDate, endDate)
                showAddTaskDialog = false
            },
            initialDate = LocalDate.now()
        )
    }
}
