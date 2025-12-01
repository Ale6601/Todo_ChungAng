package kr.mobile.apps.todochungang.ui.tasks


import kr.mobile.apps.todochungang.data.Task
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column // Column import
import androidx.compose.foundation.layout.fillMaxSize // fillMaxSize import
import androidx.compose.foundation.layout.padding // padding import
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog // Dialog import
import androidx.compose.material3.CircularProgressIndicator // 로딩 인디케이터
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Add // 세부 정보 아이콘
import androidx.compose.material.icons.Icons // Icons.* 를 사용하기 위한 기본 import
import androidx.compose.material.icons.filled.Close // 삭제 버튼 아이콘
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.LocalTime
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog // AlertDialog 사용
import androidx.compose.foundation.layout.Box // Box 사용
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Delete

private val LightGrayBackground = Color(0xFFF3F3F3)
private val DateRangeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
private fun formatTaskDateRange(startDate: LocalDate?, endDate: LocalDate?): String? {
    // 1. 시작일이 없으면 날짜 범위를 표시할 수 없습니다.
    if (startDate == null) return null

    val startPart = startDate.format(DateRangeFormatter)

    // 2. 마감일이 없거나 시작일과 같으면 시작일만 표시
    if (endDate == null || endDate == startDate) {
        return startPart
    }

    val endPart = endDate.format(DateRangeFormatter)

    // 3. 시작일과 마감일이 다르면 범위로 표시
    return "$startPart ~ $endPart"
}
@Composable
fun TasksScreen(viewModel: TasksViewModel = viewModel()) {

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
        containerColor = LightGrayBackground,
        floatingActionButton = {
            AddTaskFab(onClick = {
                showAddTaskDialog = true
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "My Tasks", style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilterTabRow( // 탭 UI
                currentFilter = currentFilter,
                onFilterSelected = viewModel::setFilter,
                allCount = allCount, // ✨ [추가]
                activeCount = activeCount, // ✨ [추가]
                completedCount = completedCount // ✨ [추가]
            )

            // ✨ [추가] 완료 탭일 때만 "모두 삭제" 버튼 표시 로직
            if (currentFilter == TaskFilter.COMPLETED && tasks.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.deleteAllCompletedTasks() }, // 💡 [함수 호출]
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("모든 완료 항목 삭제")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // 버튼과 목록 사이 간격
            } else {
                Spacer(modifier = Modifier.height(16.dp)) // 삭제 버튼이 없을 때의 간격 유지
            }

            // 💡 [삽입 위치]: 로딩 피드백 박스 코드
            if (isAddingTask && taskTitleLoading.isNotBlank()) {
                LoadingFeedbackBox(taskTitle = taskTitleLoading)
                Spacer(modifier = Modifier.height(16.dp)) // 로딩 박스와 목록 사이 간격
            }


            // 3. [UPDATE] Task 목록 표시 영역
            Box(modifier = Modifier.weight(1f)) {

                // 💡 [UPDATE] Task 목록 상태 확인 로직 (단일 'tasks' 목록으로 확인)
                if (tasks.isEmpty() && !isAddingTask) {
                    EmptyStateMessage()
                } else {
                    // 💡 [UPDATE] TaskList 호출 (단일 목록 전달)
                    TaskList(
                        tasks = tasks, // 💡 필터링된 단일 목록 전달
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
                viewModel.deleteTask(task) // ViewModel의 삭제 함수 호출
                selectedTask = null        // Dialog 닫기
            },
            onToggleCompleted = { isChecked ->
                // ViewModel의 toggleCompletion 함수는 Task 객체를 요구하므로,
                // 현재 Task 객체의 isCompleted 상태를 변경한 복사본을 만들어 전달합니다.
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


// --------------------------------------------------------
// @Composable fun TaskItem(...) { ... } // TaskItem 함수가 이어서 위치해야 합니다.
    // Task 목록의 각 항목(한 줄)을 표시하는 컴포넌트
// TasksScreen.kt (TaskItem 함수)

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onTaskClick(task) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🟢 [추가] 1. 원형 체크박스 스타일 Icon 구현 시작
        // 💡 [필수 Import 확인]: Icons.Filled.CheckCircle, Icons.Outlined.RadioButtonUnchecked 가 필요합니다.
        val checkboxIcon = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked
        val checkboxColor = if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray // 완료 시 주 색상 사용

        IconButton(
            onClick = { onToggleComplete(task) }, // 클릭 시 완료 상태 토글 기능 유지
            modifier = Modifier.size(48.dp) // 💡 터치 영역 확장 (Google 표준)
        ) {
            Icon(
                imageVector = checkboxIcon,
                contentDescription = "Task 완료 상태",
                tint = checkboxColor,
                modifier = Modifier.size(24.dp) // 아이콘 크기 설정
            )
        }

        Spacer(Modifier.width(8.dp))

        // 💡 [수정] Task 이름과 날짜를 세로로 배열하기 위해 Column 사용
        Column(modifier = Modifier.weight(1f)) {

            // 1. Task 제목
            Text(
                text = task.title.replace('\n', ' '),
                modifier = Modifier.fillMaxWidth(), // Column 내에서 가로 공간 채우기
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) Color.Gray else Color.Black
                )
            )
            val dateRangeText = formatTaskDateRange(task.startDate, task.endDate)
            // 2. ✨ [추가] 날짜/시간 정보 표시
            if (dateRangeText != null) { // 날짜 범위가 있을 경우에만 표시
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 💡 [추가] 달력 아이콘 (Event Icon)
                    Icon(
                        Icons.Filled.Event,
                        contentDescription = "기한 범위",
                        modifier = Modifier.size(16.dp),
                        tint = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = dateRangeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        } // Column 종료

        // 삭제 버튼
        IconButton(onClick = { onDeleteTask(task) }) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Task 삭제", tint = Color.Red)
        }
    }
}

    // Task 목록 전체를 스크롤 가능하게 표시하는 컴포넌트
// TasksScreen.kt (TaskList 함수 전체 대체)

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,
) {
    // 1. Task 목록 (LazyColumn)
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
    fun EmptyStateMessage() {
        Column(
            // 화면 전체를 채우지만 상단에 정렬하고, 가로 중앙에 텍스트를 배치합니다.
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "새로운 할 일을 추가해 보세요!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray
            )
        }
    }

    @Composable
    fun LoadingFeedbackBox(taskTitle: String) {
        Surface(
            // 모서리 둥글게 처리
            shape = RoundedCornerShape(8.dp),
            // 배경색을 테마의 보조색으로 지정하여 눈에 띄게 합니다.
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .fillMaxWidth() // 가로 전체 사용
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. 로딩 인디케이터 (돌아가는 원)
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))

                // 2. 메시지 (텍스트)
                Text(
                    text = "'${taskTitle}' 추가 중...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }


    // TasksScreen.kt 파일 하단에 추가

    @Composable
    fun AddTaskFab(onClick: () -> Unit) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            // 배경색을 테마의 보조색으로 지정
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            // 내용물의 색상 지정
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            // 아이콘 (더하기)
            icon = { Icon(Icons.Filled.Add, contentDescription = "할 일 추가") },
            // 텍스트 (할 일 추가)
            text = { Text("할 일 추가") },
            // 버튼의 모양 (모서리 둥글게)
            shape = RoundedCornerShape(16.dp),
            // 그림자 높이 지정
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
        )
    }


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
            ) -> Unit, // 💡 date 매개변수 추가
    initialDate: LocalDate
) {
    // 1. Task 제목 입력 상태
    var taskTitle by remember { mutableStateOf("") }
    var taskDetails by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var pickingEndDate by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(initialDate) } // 시작일은 현재 날짜로 초기화
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var showTimePicker by remember { mutableStateOf(false) }
    var pickingEndTime by remember { mutableStateOf(false) } // 마감 시간 선택 중 여부
    var startTime by remember { mutableStateOf<LocalTime?>(null) } // 시작 시간
    var endTime by remember { mutableStateOf<LocalTime?>(null) }   // 마감 시간

    val isAddButtonEnabled = taskTitle.isNotBlank()

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
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
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
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("취소") } }
        ) { DatePicker(state = dateState) }
    }
    if (showTimePicker) {
        TaskTimePickerDialog(
            initialTime = if (pickingEndTime) endTime else startTime, // ✨ [수정] pickingEndTime에 따라 다른 시간 전달
            onTimeSelected = { newTime ->
                if (pickingEndTime) endTime = newTime else startTime = newTime // ✨ [수정] 시간 상태 업데이트
            },
            onDismiss = { showTimePicker = false })
    }


    // 4. Dialog (Google Tasks 스타일 모달 역할을 대신합니다)
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "새 할 일 추가",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // 입력 필드 (Text Input)
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("할 일 제목") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // ✨ [추가] 2. 세부 정보 입력 필드
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
                    verticalArrangement = Arrangement.spacedBy(8.dp) // 날짜 Row와 시간 Row 사이 간격
                ) {
                    // ✨ [수정] 3. 날짜 표시 및 버튼
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 1A. 시작일 설정 버튼
                        TextButton(onClick = { pickingEndDate = false; showDatePicker = true },modifier = Modifier.weight(1f)) {
                            Icon(
                                Icons.Filled.Event,
                                contentDescription = "시작일 변경",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                startDate.format(
                                    DateTimeFormatter.ofPattern(
                                        "M월 d일",
                                        Locale.KOREA
                                    )
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("~") // 범위 표시자
                        Spacer(modifier = Modifier.width(8.dp))

                        // 1B. 마감일 설정 버튼
                        TextButton(onClick = { pickingEndDate = true; showDatePicker = true },modifier = Modifier.weight(1f)) {
                            Icon(
                                Icons.Filled.Event,
                                contentDescription = "마감일 변경",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                endDate?.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
                                    ?: "마감일"
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 2A. 시작 시간 설정 버튼
                        TextButton(onClick = { pickingEndTime = false; showTimePicker = true },modifier = Modifier.weight(1f)) {
                            val timeText = startTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "시작 시간"
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
                        // 2B. 마감 시간 설정 버튼
                        TextButton(onClick = { pickingEndTime = true; showTimePicker = true },modifier = Modifier.weight(1f)) {
                            val timeText =
                                endTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "마감 시간"
                            Icon(
                                Icons.Filled.Schedule,
                                contentDescription = "마감 시간 설정",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(timeText)
                        }

                        // 💡 시간 지우기 버튼 (시작 시간과 마감 시간 중 하나라도 설정되어 있을 때 표시)
                        if (startTime != null || endTime != null) {
                            IconButton(onClick = { startTime = null; endTime = null }) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "시간 지우기",
                                    tint = Color.Gray
                                )
                            }
                        }
                    } // Row 종료
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 액션 버튼 영역
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("취소") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            // 🟢 [최종 수정] 6개 인자(title, details, time, startDate, endDate)를 정확히 전달
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
// TasksScreen.kt (TaskDetailDialog 함수를 아래 코드로 전체 교체)

@Composable
fun TaskDetailDialog(
    task: Task,
    viewModel: TasksViewModel,
    onDismiss: () -> Unit,
    onDeleteTask: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    // 1. 상태 정의 (수정 가능한 상태)
    var currentTitle by remember { mutableStateOf(task.title) } // 🟢 [추가] 제목 편집 상태
    var currentDetails by remember { mutableStateOf(task.details) }

    // 2. 날짜/시간 상태 (Task 모델의 값을 초기값으로 사용)
    var currentStartDate by remember { mutableStateOf(task.startDate) }
    var currentEndDate by remember { mutableStateOf(task.endDate) }
    var currentStartTime by remember { mutableStateOf(task.startTime) }
    var currentEndTime by remember { mutableStateOf(task.endTime) }

    var isDatePickerShowing by remember { mutableStateOf(false) }
    var isTimePickerShowing by remember { mutableStateOf(false) }
    var pickingEndTime by remember { mutableStateOf(false) }
    var isDetailsExpanded by remember { mutableStateOf(task.details.isNotBlank()) }

    val formatter = remember { DateTimeFormatter.ofPattern("yyyy. M . d") }


    // DatePicker/TimePicker Calls (Outside Dialog body)
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
                // 1. Task 제목 및 삭제 버튼 헤더 (유지)
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
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
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

                // 2. 완료 상태 (Interactive Checkbox)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .offset(x = (-8).dp)
                        .padding(bottom = 12.dp)
                ) {
                    Checkbox(checked = task.isCompleted, onCheckedChange = onToggleCompleted)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (task.isCompleted) "완료됨" else "미완료",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.isCompleted) Color.Gray else Color.Black
                    )
                }

                // -----------------------------------------------------
                // 3. ✨ [최종 FIX] 기한 (날짜 및 시간 편집 그룹) - 간소화된 버튼 UI
                // -----------------------------------------------------
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 3A. 📅 날짜 범위 Row (시작일 ~ 마감일)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 시작일 버튼
                        TextButton(
                            onClick = { isDatePickerShowing = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            // 🟢 [FIX] "시작일: " 레이블 제거
                            Text(text = currentStartDate?.format(formatter) ?: "시작일 설정")
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("~") // 범위 표시자
                        Spacer(modifier = Modifier.width(8.dp))

                        // 마감일 설정 버튼
                        TextButton(
                            onClick = { isDatePickerShowing = true },
                            modifier = Modifier.weight(1f)
                        ) {

                            // 🟢 [FIX] "마감일: " 레이블 제거
                            Text(text = currentEndDate?.format(formatter) ?: "마감일 설정")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // 날짜 섹션과 시간 섹션 사이 간격

// 3B. ⏱️ 시간 설정 Row (이어서 배치)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 시작 시간 설정 버튼
                    TextButton(onClick = { pickingEndTime = false; isTimePickerShowing = true }) {
                        val startTimeText = currentStartTime?.format(DateTimeFormatter.ofPattern("a h:mm")) ?: "시작 시간"
                        Icon(Icons.Filled.Schedule, contentDescription = "시작 시간 설정", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(startTimeText)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text("~")
                    Spacer(modifier = Modifier.width(8.dp))

                    // 마감 시간 설정 버튼
                    TextButton(onClick = { pickingEndTime = true; isTimePickerShowing = true }) {
                        val endTimeText = currentEndTime?.format(DateTimeFormatter.ofPattern("a h:mm")) ?: "마감 시간"
                        Icon(Icons.Filled.Schedule, contentDescription = "마감 시간 설정", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(endTimeText)
                    }

                    // 시간 지우기 버튼
                    if (currentStartTime != null || currentEndTime != null) {
                        IconButton(onClick = { currentStartTime = null; currentEndTime = null }) {
                            Icon(Icons.Filled.Close, contentDescription = "시간 지우기", tint = Color.Gray)
                        }
                    }
                } // Row 종료 (시간 그룹)

                Spacer(modifier = Modifier.height(16.dp)) // 날짜/시간 그룹과 상세 정보 섹션 사이 간격

                if (isDetailsExpanded) {
                    // 4. 세부 정보 (편집 가능한 TextField)
                    OutlinedTextField(
                        value = currentDetails,
                        onValueChange = { currentDetails = it },
                        label = if (currentDetails.isEmpty()) { { Text("세부 정보") } } else null,
                        placeholder = if (currentDetails.isEmpty()) { { Text("세부 정보를 입력하세요...", color = Color.Gray) } } else null,
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = "세부 정보 아이콘") },
                        minLines = 3, maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, disabledContainerColor = MaterialTheme.colorScheme.surface)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { isDetailsExpanded = false }) { Text("숨기기") }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                else {
                    // 4b. 축소된 상태: 클릭 가능한 버튼 UI 표시
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { isDetailsExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = "세부 정보 아이콘", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = if (task.details.isNotBlank()) task.details.take(20) + if (task.details.length > 20) "..." else "" else "세부 정보 추가",
                            color = if (task.details.isNotBlank()) Color.DarkGray else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 5. 저장 및 닫기 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        // ViewModel 함수를 호출하여 업데이트
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


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskDatePickerDialog(
        initialDate: LocalDate?,
        onDateSelected: (LocalDate) -> Unit,
        onDismiss: () -> Unit
    ) {
        val actualInitialDate = initialDate ?: LocalDate.now()
        // LocalDate를 Long (millis)로 변환하여 초기 상태 설정
        val initialTimeMillis =
            actualInitialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = initialTimeMillis
        )

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            // 선택된 millis를 LocalDate로 변환하여 콜백 함수 호출
                            val newDate =
                                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            onDateSelected(newDate)
                        }
                        onDismiss()
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskTimePickerDialog(
        initialTime: LocalTime?,
        onTimeSelected: (LocalTime) -> Unit,
        onDismiss: () -> Unit
    ) {
        // 1. TimePickerState 정의
        val now = LocalTime.now()
        val initialHour = initialTime?.hour ?: now.hour
        val initialMinute = initialTime?.minute ?: now.minute

        val timeState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = false // 12시간제로 표시
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("시간 설정")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime = LocalTime.of(timeState.hour, timeState.minute)
                        onTimeSelected(selectedTime)
                        onDismiss()
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소")
                }
            },
            text = {
                // 🟢 [TimeInput Composable] 숫자 입력 필드 스타일로 시간 선택 UI 제공
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimeInput(state = timeState)
                }
            }
        )
    }


// TasksScreen.kt (파일 하단에 추가)


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
        modifier = Modifier.fillMaxWidth(),
        divider = { HorizontalDivider(color = Color.Transparent) }
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
                // 탭 텍스트 설정 (ALL -> All, ACTIVE -> Active, COMPLETED -> Completed)
                text = {
                    val tabName = filter.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    Text(text = "$tabName ($count)")
                },
                // 선택된 탭의 색상을 강조하고, 선택되지 않은 탭의 색상을 조정하여
                // 목표 UI의 Segmented Button 느낌을 낼 수 있습니다.
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    // 탭 아래에 구분선을 추가하여 상단 AppBar와 분리
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

