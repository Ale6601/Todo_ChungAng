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

    // TasksRepository.tasks 가 Compose 상태일 가능성이 있어서 snapshotFlow 사용
    private val tasksFlow = snapshotFlow { TasksRepository.tasks }

    init {
        // Task 목록이나 현재 Month 가 바뀔 때마다 이벤트 다시 계산
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

    /**
     * Task 리스트를 캘린더에서 사용할 CalendarEvent 리스트로 변환
     * - startDate/endDate 가 없으면 creationDate 기준으로 1일짜리 이벤트로 처리
     * - 현재 month 와 겹치는 일정만 남김
     */
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

            // 현재 달과 전혀 겹치지 않는 Task 는 제외
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
