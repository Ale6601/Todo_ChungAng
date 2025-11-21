package kr.mobile.apps.todochungang.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.mobile.apps.todochungang.data.Task
import kr.mobile.apps.todochungang.data.repository.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime


class TasksViewModel : ViewModel() {

    // 로딩 상태 및 제목 추적 StateFlow
    private val _isAddingTask = MutableStateFlow(false)
    val isAddingTask: StateFlow<Boolean> = _isAddingTask.asStateFlow()
    private val _taskTitleLoading = MutableStateFlow("")
    val taskTitleLoading: StateFlow<String> = _taskTitleLoading.asStateFlow()

    // 💡 [추가] 완료된 섹션의 확장 상태
    private val _isCompletedSectionExpanded = MutableStateFlow(false)
    val isCompletedSectionExpanded: StateFlow<Boolean> = _isCompletedSectionExpanded.asStateFlow()

    // 💡 [수정] 1. 미완료 Task 목록 (UI 상단 표시용)
    val incompleteTasks: List<Task>
        get() = TasksRepository.tasks.filter { !it.isCompleted }

    // 💡 [수정] 2. 완료 Task 목록 (접히는 섹션 표시용, 정렬 유지)
    val completedTasks: List<Task>
        get() = TasksRepository.tasks.filter { it.isCompleted }.sortedByDescending { it.dueDate }


    // Task 추가 로직 (비동기 및 로딩 상태 제어 추가)
    fun addTask(title: String, date: LocalDate, details: String, time: LocalTime?) {
        if (title.isBlank() || _isAddingTask.value) return

        viewModelScope.launch {
            _isAddingTask.value = true
            _taskTitleLoading.value = title

            try {
                val newTask = Task(title = title, dueDate = date, details = details, dueTime = time)
                TasksRepository.addTask(newTask)

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
        TasksRepository.toggleTaskCompletion(task)
    }

    // Task 삭제 로직은 그대로 유지
    fun deleteTask(task: Task) {
        TasksRepository.deleteTask(task)
    }

    fun updateTaskDetails(taskId: String, newDetails: String, newDate: LocalDate, newTime: LocalTime?) {
        // ViewModel은 Repository에 데이터 변경을 위임합니다.
        TasksRepository.updateTask(taskId, newDetails, newDate, newTime)
    }

    // 💡 [수정] 3. 완료 섹션 확장/축소 토글 함수
    fun toggleCompletedSectionExpansion() {
        _isCompletedSectionExpanded.value = !_isCompletedSectionExpanded.value
    }


}