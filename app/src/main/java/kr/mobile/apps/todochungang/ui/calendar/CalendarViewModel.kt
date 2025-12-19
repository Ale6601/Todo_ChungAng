package kr.mobile.apps.todochungang.ui.calendar

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.mobile.apps.todochungang.data.model.Task
import kr.mobile.apps.todochungang.data.repository.TasksRepository
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val events: List<CalendarEvent> = emptyList()
)

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    
    private val tasksFlow = snapshotFlow { TasksRepository.tasks }

    init {
        
        viewModelScope.launch {
            combine(
                tasksFlow,
                _uiState.map { it.currentMonth }.distinctUntilChanged()
            ) { tasks, month ->
                buildEventsForMonth(tasks, month)
            }.collect { events ->
                _uiState.update { it.copy(events = events) }
            }
        }
    }

    fun changeMonth(month: YearMonth) {
        _uiState.update { it.copy(currentMonth = month) }
    }

    private fun buildEventsForMonth(
        tasks: List<Task>,
        month: YearMonth
    ): List<CalendarEvent> {
        val monthStart: LocalDate = month.atDay(1)
        val monthEnd: LocalDate = month.atEndOfMonth()

        return tasks.mapNotNull { task ->
            val start: LocalDate = task.startDate ?: task.creationDate
            val end: LocalDate =
                task.endDate ?: task.startDate ?: task.creationDate

            
            if (end.isBefore(monthStart) || start.isAfter(monthEnd)) {
                return@mapNotNull null
            }

            CalendarEvent(
                id = task.id,
                title = task.title,
                start = start,
                end = end
            )
        }
    }
}
