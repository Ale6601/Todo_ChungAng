package kr.mobile.apps.todochungang.data.repository

import androidx.compose.runtime.mutableStateListOf
import kr.mobile.apps.todochungang.data.Task
import kotlinx.coroutines.delay
import java.time.LocalDate // 💡 [추가] LocalDate 사용을 위한 import
import java.time.LocalTime

// Singleton 객체: 앱이 실행되는 동안 Task 데이터를 메모리에 보관합니다.
object TasksRepository {

    private val _tasks = mutableStateListOf<Task>()

    // ViewModel이 이 목록을 읽을 수 있도록 제공합니다.
    val tasks = _tasks

    // 새 Task를 추가하는 로직 (suspend 함수)
    suspend fun addTask(task: Task) {
        delay(500) // 0.5초 딜레이 (로딩 테스트용)
        _tasks.add(task)
    }

    // Task 완료 상태를 변경하는 로직 (기존과 동일)
    fun toggleTaskCompletion(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            // Task 객체를 복사하고 isCompleted 상태만 반전시켜 목록을 갱신합니다.
            _tasks[index] = task.copy(isCompleted = !task.isCompleted)
            // 정렬된 목록을 유지하기 위해 별도 정렬 로직이 필요하지만, 여기서는 상태 업데이트만 처리합니다.
        }
    }

    // 💡 [추가] Task의 세부 정보와 날짜를 수정하는 로직
    fun updateTask(taskId: String, newDetails: String, newDate: LocalDate, newTime: LocalTime?) {
        // 1. 해당 Task의 인덱스 찾기
        val index = _tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            // 2. 기존 Task 복사 및 내용 업데이트
            val existingTask = _tasks[index]
            val updatedTask = existingTask.copy(
                details = newDetails, // 세부 정보 수정
                dueDate = newDate, // 날짜 수정
                dueTime = newTime
            )

            // 3. 목록의 해당 위치 Task를 업데이트된 Task로 교체하여 UI에 반영
            _tasks[index] = updatedTask
        }
    }

    // Task를 목록에서 삭제하는 로직 (기존과 동일)
    fun deleteTask(task: Task) {
        _tasks.remove(task)
    }
}