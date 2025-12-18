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
import kr.mobile.apps.todochungang.data.model.Task
import kr.mobile.apps.todochungang.data.repository.TasksRepository
import java.time.LocalDate
import java.time.LocalTime

enum class TaskFilter { ALL, ACTIVE, COMPLETED }
class TasksViewModel : ViewModel() {
    private val _isAddingTask = MutableStateFlow(false)
    val isAddingTask: StateFlow<Boolean> = _isAddingTask.asStateFlow()
    private val _taskTitleLoading = MutableStateFlow("")
    val taskTitleLoading: StateFlow<String> = _taskTitleLoading.asStateFlow()

    private val _currentFilter = MutableStateFlow(TaskFilter.ALL)
    val currentFilter: StateFlow<TaskFilter> = _currentFilter.asStateFlow()
    private val _updateSignal = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }
    private val _tasksFlow = snapshotFlow { TasksRepository.tasks }

    val filteredTasks: StateFlow<List<Task>> =
        _updateSignal.combine(_tasksFlow) { _, tasks -> tasks }
            .combine(currentFilter) { tasks, filter ->
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


    fun setFilter(filter: TaskFilter) {
        _currentFilter.value = filter
        _updateSignal.tryEmit(Unit)
    }


    fun addTask(
        title: String,
        details: String,
        startTime: LocalTime?,
        endTime: LocalTime?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ) {
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
                    startDate = startDate,
                    endDate = endDate
                )
                TasksRepository.addTask(newTask)
                _updateSignal.tryEmit(Unit)

            } catch (e: Exception) {
                println("Error adding task: ${e.message}")
            } finally {
                _isAddingTask.value = false
                _taskTitleLoading.value = ""
            }
        }
    }


    fun toggleTaskCompletion(task: Task) {

        TasksRepository.toggleTaskCompletion(task)
        _updateSignal.tryEmit(Unit)
    }

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

        TasksRepository.updateTask(
            taskId, newDetails, newStartDate, newEndDate, newStartTime, newEndTime
        )
        _updateSignal.tryEmit(Unit)
    }

    fun deleteAllCompletedTasks() {
        TasksRepository.deleteAllCompletedTasks()
        _updateSignal.tryEmit(Unit)
    }


}