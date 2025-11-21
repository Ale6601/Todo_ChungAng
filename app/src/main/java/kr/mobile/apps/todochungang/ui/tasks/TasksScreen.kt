package kr.mobile.apps.todochungang.ui.tasks


import kr.mobile.apps.todochungang.data.Task
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog // Dialog import
import androidx.compose.material3.CircularProgressIndicator // 로딩 인디케이터
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.CalendarMonth // 세부 정보 아이콘
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.RadioButtonUnchecked

private val LightGrayBackground = Color(0xFFF3F3F3)
@Composable
fun TasksScreen(viewModel: TasksViewModel = viewModel()) {

    // 💡 [수정] 중복된 taskTitleLoading 정의를 제거하고, isAddingTask만 남깁니다.
    val isAddingTask by viewModel.isAddingTask.collectAsState(initial = false)
    val taskTitleLoading by viewModel.taskTitleLoading.collectAsState(initial = "") // 이 변수는 유지

    val isCompletedSectionExpanded by viewModel.isCompletedSectionExpanded.collectAsState()

    // 💡 [추가] ViewModel에서 분리된 두 목록을 가져옵니다.
    val incompleteTasks = viewModel.incompleteTasks
    val completedTasks = viewModel.completedTasks
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }


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
                text = "My Tasks", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // 💡 [삽입 위치 2]: 로딩 피드백 박스 코드
            if (isAddingTask && taskTitleLoading.isNotBlank()) {
                LoadingFeedbackBox(taskTitle = taskTitleLoading)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 💡 [수정] Task 목록 상태 확인 로직 (incompleteTasks 사용)
            Box(modifier = Modifier.weight(1f)) {

                // 1. Task 목록 (TaskList 호출)
                TaskList(
                    incompleteTasks = incompleteTasks, // 미완료 목록 전달
                    completedTasks = completedTasks,   // 완료 목록 전달
                    isCompletedSectionExpanded = isCompletedSectionExpanded, // 확장 상태 전달
                    onToggleExpand = viewModel::toggleCompletedSectionExpansion, // 토글 함수 전달
                    onToggleComplete = viewModel::toggleTaskCompletion,
                    onDeleteTask = viewModel::deleteTask,
                    onTaskClick = { task -> selectedTask = task }
                )

                // 💡 [추가] EmptyStateMessage 조건 (미완료 Task도 없고, 완료 Task도 없을 때)
                if (incompleteTasks.isEmpty() && completedTasks.isEmpty() && !isAddingTask) {
                    EmptyStateMessage()
                }
            }
        }
    }

            selectedTask?.let { task ->
                TaskDetailDialog(
                    task = task,
                    viewModel= viewModel,
                    onDismiss = { selectedTask = null }
                )
            }
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, date, details, time -> // 💡 [수정] 세 번째 인자 'details' 추가
                // ViewModel의 addTask 함수를 세 개의 인자와 함께 호출
                viewModel.addTask(title, date, details, time)
                showAddTaskDialog = false
            },
            initialDate = selectedDate
        )
    }
}

// 날짜/시간 포맷터 정의 및 포맷팅 로직 (파일 최상단 레벨에 배치)
private val DateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH) // 예: Nov 21
private val TimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH) // 예: 4:00 AM

// 현재 날짜를 가져오는 함수 (Today, Nov 21 등을 결정하기 위해)
private fun formatTaskDate(date: LocalDate, time: LocalTime?): String {
    val today = LocalDate.now()
    val isToday = date == today

    // 1. 날짜 부분 포맷팅 (오늘이면 Today, 아니면 월 일)
    val datePart = if (isToday) {
        "Today"
    } else {
        date.format(DateFormatter)
    }

    // 2. 시간 부분 포맷팅
    val timePart = time?.let {
        ", ${it.format(TimeFormatter)}"
    } ?: ""

    // 3. 최종 결합
    return datePart + timePart
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
        // 🟢 [추가] 원형 체크박스 스타일 Icon 구현 끝

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

            // 2. ✨ [추가] 날짜/시간 정보 표시
            if (task.dueDate != null) {
                // 날짜/시간 포맷팅 로직 사용 (formatTaskDate 함수 사용 가정)
                val dateText = formatTaskDate(task.dueDate, task.dueTime)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
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
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,

    // 💡 [추가된 매개변수들]
    incompleteTasks: List<Task>, // 미완료 Task 목록
    completedTasks: List<Task>,   // 완료 Task 목록
    isCompletedSectionExpanded: Boolean, // 확장/축소 상태
    onToggleExpand: () -> Unit // 확장/축소 함수
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        // 1. 미완료 Task 목록
        items(incompleteTasks, key = { it.id }) { task ->
            TaskItem(task = task, onToggleComplete = onToggleComplete, onDeleteTask = onDeleteTask, onTaskClick = onTaskClick)
            HorizontalDivider()
        }

        // 2. 완료 Task 섹션 헤더 (완료된 Task가 있을 경우에만 표시)
        if (completedTasks.isNotEmpty()) {
            item {
                CompletedHeader(
                    count = completedTasks.size,
                    isExpanded = isCompletedSectionExpanded,
                    onClick = onToggleExpand // 토글 액션 연결
                )
            }

            // 3. 완료 Task 목록 (접기/펴기)
            if (isCompletedSectionExpanded) {
                items(completedTasks, key = { it.id }) { task ->
                    TaskItem(task = task, onToggleComplete = onToggleComplete, onDeleteTask = onDeleteTask, onTaskClick = onTaskClick)
                    HorizontalDivider()
                }
            }
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
    onAddTask: (title: String, date: LocalDate, details: String, time: LocalTime?) -> Unit, // 💡 date 매개변수 추가
    initialDate: LocalDate
) {
    // 1. Task 제목 입력 상태
    var taskTitle by remember { mutableStateOf("") }
    var taskDetails by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) } // LocalTime은 nullable

    val isAddButtonEnabled = taskTitle.isNotBlank()

    if (showDatePicker) {
        val dateState = rememberDatePickerState(
            // LocalDate를 millis로 변환하여 초기 상태 설정
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            // 선택된 millis를 LocalDate로 변환하여 selectedDate 업데이트
                            selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
    // 4. Dialog (Google Tasks 스타일 모달 역할을 대신합니다)
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "새 할 일 추가",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (showTimePicker) {
                    TaskTimePickerDialog(
                        initialTime = selectedTime,
                        onTimeSelected = { newTime -> selectedTime = newTime },
                        onDismiss = { showTimePicker = false }
                    )
                }
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

                // ✨ [수정] 3. 날짜 표시 및 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(), // 상단에 충분한 간격 확보
                    horizontalArrangement = Arrangement.Start, // 💡 [핵심] 왼쪽에 정렬,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ✨ 4. 날짜 변경 버튼
                    TextButton(onClick = { showDatePicker = true }) { // 클릭 시 DatePicker 열기
                        Icon(Icons.Filled.EditCalendar, contentDescription = "날짜 변경",tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedDate.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                TextButton(onClick = { showTimePicker = true }) {
                    val timeText = selectedTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "시간 설정"

                    // 💡 [수정] Icon 호출을 하나만 남깁니다.
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "시간 설정",
                        tint = MaterialTheme.colorScheme.primary // 테마 주 색상으로 표시
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 💡 [수정] Text는 시간 설정 텍스트를 표시
                    Text(timeText)
                }
            }

                Spacer(modifier = Modifier.height(24.dp))

                // 액션 버튼 영역
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("취소") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onAddTask(taskTitle, selectedDate, taskDetails, selectedTime ) }, // ✨ [수정] details 전달
                        enabled = isAddButtonEnabled
                    ) {
                        Text("추가")
                    }
                }
            }
        }
    }
// TasksScreen.kt (TaskDetailDialog 함수를 아래 코드로 전체 교체)

@Composable
fun TaskDetailDialog(
    task: Task,
    viewModel: TasksViewModel, // ✨ [추가] ViewModel을 인자로 받습니다.
    onDismiss: () -> Unit
) {
    // ✨ [수정] 수정 가능한 상태를 정의합니다.
    var currentDetails by remember { mutableStateOf(task.details) }
    var currentDate by remember { mutableStateOf(task.dueDate ?: LocalDate.now()) }
    var isDatePickerShowing by remember { mutableStateOf(false) }
    var isDetailsExpanded by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월 d일") }
    var currentDueTime by remember { mutableStateOf(task.dueTime) }
    var isTimePickerShowing by remember { mutableStateOf(false) } // Time Picker 표시 상태

    if (isDatePickerShowing) {
        TaskDatePickerDialog(
            initialDate = currentDate,
            onDateSelected = { newDate -> currentDate = newDate },
            onDismiss = { isDatePickerShowing = false }
        )
    }
    if (isTimePickerShowing) {
        TaskTimePickerDialog(
            initialTime = currentDueTime,
            onTimeSelected = { newTime -> currentDueTime = newTime },
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
                // 1. Task 제목 (수정 불가, 크고 굵게 표시)
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // 2. 완료 상태 (기존과 동일)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .offset(x = (-8).dp)
                        .padding(bottom = 12.dp)
                ) {
                    val checkboxIcon = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked
                    val checkboxColor = if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray

                    Icon( // 💡 [수정] Checkbox 대신 Icon 사용 (수정 불가 상태 표시용)
                        imageVector = checkboxIcon,
                        contentDescription = "Task 완료 상태",
                        tint = checkboxColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (task.isCompleted) "완료됨" else "미완료",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.isCompleted) Color.Gray else Color.Black
                    )
                }

                // 3. 기한 (날짜 변경 버튼)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start, // 💡 왼쪽에 정렬
                    verticalAlignment = Alignment.CenterVertically
                ) {

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { isDatePickerShowing = true } // 날짜 선택기 열기
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "기한 아이콘")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentDate.format(formatter),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                    Spacer(modifier = Modifier.width(16.dp))

                    val timeText = currentDueTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "시간 설정"
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { isTimePickerShowing = true } // 시간 선택기 열기
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            Icon(Icons.Filled.Schedule, contentDescription = "시간 설정")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(timeText)
                        }
                        if (currentDueTime != null) {
                            IconButton(onClick = { currentDueTime = null }) {
                                Icon(Icons.Filled.Close, contentDescription = "시간 지우기", tint = Color.Gray)
                            }
                        }
                    }
                }
                if (isDetailsExpanded) {
                    // 4. 세부 정보 (편집 가능한 TextField)
                    OutlinedTextField(
                        value = currentDetails,
                        onValueChange = { currentDetails = it },
                        label = if (currentDetails.isEmpty()) { { Text("세부 정보") } } else null,
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
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            // 닫을 때 내용도 비워지도록 currentDetails = "" 로직을 추가할 수 있지만, 여기서는 닫기만 합니다.
                            isDetailsExpanded = false
                        }) {
                            Text("숨기기")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    // 4b. 축소된 상태: 클릭 가능한 버튼 UI 표시
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isDetailsExpanded = true } // ✨ [액션] 클릭 시 확장
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
                            text = if (task.details.isNotBlank()) task.details.take(20) + if (task.details.length > 20) "..." else ""
                            else "세부 정보 추가",
                            color = if (task.details.isNotBlank()) Color.DarkGray else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    // 💡 [if/else 블록 종료]
// ✨ [수정] 세부 정보가 끝나면 Spacer를 추가합니다.
                    Spacer(modifier = Modifier.height(24.dp))
                }
                // 5. 저장 및 닫기 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // 저장 버튼
                    TextButton(onClick = {
                        // ViewModel 함수를 호출하여 업데이트
                        viewModel.updateTaskDetails(task.id, currentDetails, currentDate, currentDueTime)
                        onDismiss() // Dialog 닫기
                    }) {
                        Text("저장")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // 닫기 버튼
                    TextButton(onClick = onDismiss) {
                        Text("닫기")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskDatePickerDialog(
        initialDate: LocalDate,
        onDateSelected: (LocalDate) -> Unit,
        onDismiss: () -> Unit
    ) {
        // LocalDate를 Long (millis)로 변환하여 초기 상태 설정
        val initialTimeMillis =
            initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

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
    val timeState = rememberTimePickerState(
        initialHour = initialTime?.hour ?: LocalTime.now().hour,
        initialMinute = initialTime?.minute ?: LocalTime.now().minute,
        is24Hour = false // 12시간제로 표시 (true로 바꾸면 24시간제)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
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
            TimePicker(state = timeState)
        }
    )
}


// TasksScreen.kt (파일 하단에 추가)

@Composable
fun CompletedHeader(count: Int, isExpanded: Boolean, onClick: () -> Unit) {
    // 💡 확장/축소 상태에 따라 아이콘을 회전시키기 위한 상태 (animateFloatAsState 사용)
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, // 펼쳐지면 180도 회전
        label = "ExpansionRotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // 클릭 시 목록 확장/축소
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 (회전 적용)
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = "토글",
            modifier = Modifier
                .rotate(rotation) // 회전 적용
                .size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // 텍스트 (Completed (N))
        Text(
            text = "Completed ($count)",
            style = MaterialTheme.typography.titleMedium,
            color = Color.DarkGray
        )
    }
}


