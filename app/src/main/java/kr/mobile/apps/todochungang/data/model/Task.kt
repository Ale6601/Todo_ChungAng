package kr.mobile.apps.todochungang.data.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val details: String = "",
    val isCompleted: Boolean = false,
    val isImportant: Boolean = false,
    val creationDate: LocalDate = LocalDate.now(),
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null

)