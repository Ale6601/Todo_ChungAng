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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import kr.mobile.apps.todochungang.data.model.Task
import kr.mobile.apps.todochungang.ui.tasks.components.AddTaskFab
import kr.mobile.apps.todochungang.ui.tasks.components.FilterTabRow
import kr.mobile.apps.todochungang.ui.tasks.components.TaskList
import kr.mobile.apps.todochungang.ui.tasks.dialogs.AddTaskDialog
import kr.mobile.apps.todochungang.ui.tasks.dialogs.TaskDetailDialog

// 화면 배경색
private val BackgroundColor = Color(0xFFFFFFFF)

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    // ViewModel 상태
    val isAddingTask by viewModel.isAddingTask.collectAsState(initial = false)
    val taskTitleLoading by viewModel.taskTitleLoading.collectAsState(initial = "")
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    // 화면 내부 상태
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // 날짜 헤더용 선택 날짜 (기본 오늘)
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }

    // 현재 선택된 날짜에 해당하는 Task 목록 (현재 탭 필터 이후)
    val tasksForSelectedDate = remember(selectedDate, filteredTasks) {
        filteredTasks.filter { task ->
            val start = task.startDate
            val end = task.endDate ?: start
            if (start == null) {
                // 시작일이 없는 Task는 모든 날짜에서 보일지 여부
                true
            } else {
                val effectiveEnd = end ?: start
                !selectedDate.isBefore(start) && !selectedDate.isAfter(effectiveEnd)
            }
        }
    }

    // 선택된 날짜 기준 카운트 (현재 탭 기준)
    val allCountForDate = tasksForSelectedDate.size
    val activeCountForDate = tasksForSelectedDate.count { !it.isCompleted }
    val completedCountForDate = tasksForSelectedDate.count { it.isCompleted }

    Scaffold(
        containerColor = BackgroundColor,
        floatingActionButton = {
            // BottomNavBar 와 겹치지 않게 약간 위로 올리기
            Box(modifier = Modifier.padding(bottom = 50.dp)) {
                AddTaskFab(onClick = { showAddTaskDialog = true })
            }
        },
    ) { _ -> // PaddingValues 파라미터 안 씀
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            // 1. 날짜 헤더 (◀ 날짜 ▶ 만, active 문구 없음)
            TaskNavigator(
                selectedDate = selectedDate,
                onPreviousDay = { selectedDate = selectedDate.minusDays(1) },
                onNextDay = { selectedDate = selectedDate.plusDays(1) }
            )

            // 2. 탭 (All / Active / Completed) - 날짜 기준 카운트 전달
            FilterTabRow(
                currentFilter = currentFilter,
                onFilterSelected = viewModel::setFilter,
                allCount = allCountForDate,
                activeCount = activeCountForDate,
                completedCount = completedCountForDate
            )

            // 완료 탭 + (해당 날짜 기준) 목록이 있을 때만 "모든 완료 항목 삭제" 버튼
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

            // 3. 로딩 피드백
            if (isAddingTask && taskTitleLoading.isNotBlank()) {
                LoadingFeedbackBox(taskTitle = taskTitleLoading)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Task 리스트 영역
            Box(modifier = Modifier.weight(1f)) {
                if (tasksForSelectedDate.isEmpty() && !isAddingTask) {
                    EmptyStateMessage()
                } else {
                    TaskList(
                        tasks = tasksForSelectedDate,
                        onToggleComplete = viewModel::toggleTaskCompletion,
                        onDeleteTask = viewModel::deleteTask,
                        onTaskClick = { task -> selectedTask = task }
                    )
                }
            }
        }
    }

    // 5. Task 상세 다이얼로그
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

    // 6. Task 추가 다이얼로그
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, details, startTime, endTime, startDate, endDate ->
                viewModel.addTask(title, details, startTime, endTime, startDate, endDate)
                showAddTaskDialog = false
            },
            // 현재 선택한 날짜를 기본값으로
            initialDate = selectedDate
        )
    }
}
