package kr.mobile.apps.todochungang.data
import java.time.LocalDate // 💡 [필수] LocalDate import 추가
import java.time.LocalTime
import java.util.UUID
// Task 항목의 데이터 구조를 정의하는 data class입니다.
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val dueDate: LocalDate? = null, // 기한 날짜
    val dueTime: LocalTime? = null, // 기한 시간
    val details: String = ""

)