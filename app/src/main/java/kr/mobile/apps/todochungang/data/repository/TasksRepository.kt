package kr.mobile.apps.todochungang.data.repository

import androidx.compose.runtime.mutableStateListOf
import kr.mobile.apps.todochungang.data.model.Task
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

object TasksRepository {

    private val _tasks = mutableStateListOf<Task>()

    val tasks = _tasks

    suspend fun addTask(task: Task) {
        delay(500)
        _tasks.add(task)
    }

    fun toggleTaskCompletion(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            _tasks.removeAt(index)
            _tasks.add(index, updatedTask)
        }
    }

    fun updateTask(
        taskId: String,
        newDetails: String,
        newStartDate: LocalDate?,
        newEndDate: LocalDate?,
        newStartTime: LocalTime?,
        newEndTime: LocalTime?
    ) {
        val index = _tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {

            val existingTask = _tasks[index]
            val updatedTask = existingTask.copy(
                details = newDetails,
                startDate = newStartDate,
                endDate = newEndDate,
                startTime = newStartTime,
                endTime = newEndTime
            )


            _tasks.removeAt(index)
            _tasks.add(index, updatedTask)
        }
    }


    fun deleteTask(task: Task) {
        _tasks.remove(task)
    }

    fun deleteAllCompletedTasks() {
        _tasks.removeAll { it.isCompleted }
    }
}