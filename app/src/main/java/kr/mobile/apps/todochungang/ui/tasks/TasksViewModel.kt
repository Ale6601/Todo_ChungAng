package kr.mobile.apps.todochungang.ui.tasks

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.mobile.apps.todochungang.data.Task
import kr.mobile.apps.todochungang.data.repository.TasksRepository
import java.time.LocalDate
import java.time.LocalTime

enum class TaskFilter { ALL, ACTIVE, COMPLETED }
class TasksViewModel : ViewModel() {

    private val allTasks: List<Task>
        get() = TasksRepository.tasks

    // 💡 [추가] Task 개수 계산
    val allCount: Int
        get() = allTasks.size

    val activeCount: Int
        get() = allTasks.count { !it.isCompleted } // 미완료(Active) Task 개수

    val completedCount: Int
        get() = allTasks.count { it.isCompleted } // 완료(Completed) Task 개수
    // 로딩 상태 및 제목 추적 StateFlow
    private val _isAddingTask = MutableStateFlow(false)
    val isAddingTask: StateFlow<Boolean> = _isAddingTask.asStateFlow()
    private val _taskTitleLoading = MutableStateFlow("")
    val taskTitleLoading: StateFlow<String> = _taskTitleLoading.asStateFlow()

    private val _currentFilter = MutableStateFlow(TaskFilter.ALL)
    val currentFilter: StateFlow<TaskFilter> = _currentFilter.asStateFlow()
    private val _updateSignal = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) } // 초기값 설정
    private val _tasksFlow = snapshotFlow { TasksRepository.tasks }
    // 💡 [수정] 3. 필터링된 Task 목록을 계산하는 Computed Property
    val filteredTasks: StateFlow<List<Task>> = _updateSignal
        .combine(_tasksFlow) { _, tasks -> tasks } // 트리거 발생 시 tasks 목록 가져옴
        .combine(currentFilter) { tasks, filter -> // 필터와 결합하여 최종 목록 계산
            val filteredList = tasks.filter { task ->
                when (filter) {
                    TaskFilter.ALL -> true
                    TaskFilter.ACTIVE -> !task.isCompleted
                    TaskFilter.COMPLETED -> task.isCompleted
                }
            }
            filteredList.sortedBy { it.isCompleted }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 💡 [추가] 4. 필터를 변경하는 함수
    fun setFilter(filter: TaskFilter) {
        _currentFilter.value = filter
        _updateSignal.tryEmit(Unit)
    }

    // Task 추가 로직 (비동기 및 로딩 상태 제어 추가)
    fun addTask(title: String, details: String, startTime: LocalTime?, endTime: LocalTime?, startDate: LocalDate?, endDate: LocalDate?) {
        if (title.isBlank() || _isAddingTask.value) return

        viewModelScope.launch {
            _isAddingTask.value = true
            _taskTitleLoading.value = title

            try {
                val newTask = Task(
                    title = title,
                    details = details,
                    startTime = startTime,
                    endTime = endTime,
                    startDate = startDate, // 🟢 FIX: startDate 할당
                    endDate = endDate
                )
                TasksRepository.addTask(newTask)
                _updateSignal.tryEmit(Unit) // 업데이트 신호 발생

            } catch (e: Exception) {
                println("Error adding task: ${e.message}")
            } finally {
                _isAddingTask.value = false
                _taskTitleLoading.value = ""
            }
        }
    }

    // Task 완료 상태 변경 로직은 그대로 유지
    fun toggleTaskCompletion(task: Task) {
        // 💡 [수정] 복잡한 인덱싱 로직을 제거하고 Repository의 함수를 호출하여 위임합니다.
        TasksRepository.toggleTaskCompletion(task)
        _updateSignal.tryEmit(Unit)
    }
    // Task 삭제 로직은 그대로 유지
    fun deleteTask(task: Task) {
        TasksRepository.deleteTask(task)
        _updateSignal.tryEmit(Unit)
    }
    fun updateTaskDetails(
        taskId: String,
        newDetails: String,
        newStartDate: LocalDate?,
        newEndDate: LocalDate?,
        newStartTime: LocalTime?,
        newEndTime: LocalTime?
    ) {
        // ViewModel은 Repository에 데이터 변경을 위임합니다.
        TasksRepository.updateTask(taskId, newDetails, newStartDate,newEndDate, newStartTime, newEndTime)
        _updateSignal.tryEmit(Unit)
    }

    fun deleteAllCompletedTasks() {
        TasksRepository.deleteAllCompletedTasks()
        _updateSignal.tryEmit(Unit)
    }


}