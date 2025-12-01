package kr.mobile.apps.todochungang.data

import java.time.LocalDate // 💡 [필수] LocalDate import 추가
import java.time.LocalTime

// Task 항목의 데이터 구조를 정의하는 data class입니다.

data class Task(
    val id: String = java.util.UUID.randomUUID().toString(),
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