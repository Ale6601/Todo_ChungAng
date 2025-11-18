package kr.mobile.apps.todochungang.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun TasksScreen() {
    // ⚠️ 기존의 Box와 Text 코드를 이 Column 코드로 대체하세요.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // 전체 화면에 여백을 줍니다.
    ) {
        // 1. 화면 상단 제목
        Text(
            text = "My Tasks",
            style = MaterialTheme.typography.headlineLarge // 큰 글자 스타일 사용
        )
        Spacer(modifier = Modifier.height(24.dp)) // 제목 아래에 간격을 줍니다.

        AddTaskInput(onAddTask = { taskName ->
            println("할 일 추가 요청: $taskName") // 나중에 실제 데이터 추가 로직으로 대체
        })
        Spacer(modifier = Modifier.height(16.dp))



        // 3. [추가 예정] 할 일 목록 컴포넌트
        TaskList()
    }
}

@Composable
fun AddTaskInput(
    onAddTask: (String) -> Unit // 새 Task가 입력되면 호출될 함수 정의
) {
    // 입력된 텍스트를 저장하는 상태 변수
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 텍스트 입력 필드
        OutlinedTextField(
            value = text,
            onValueChange = { text = it }, // 입력값이 바뀔 때마다 상태 갱신
            label = { Text("새 할 일 입력") },
            modifier = Modifier.weight(1f), // 남은 공간을 모두 차지하도록 설정
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 추가 버튼
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onAddTask(text) // Task 추가 함수 호출
                    text = "" // 입력창 비우기
                }
            },
            // 입력창이 비어있으면 버튼 비활성화
            enabled = text.isNotBlank()
        ) {
            Text("추가")
        }
    }
}
// Task 목록의 각 항목(한 줄)을 표시하는 컴포넌트
@Composable
fun TaskItem(name: String, isCompleted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 할 일 완료 여부를 표시하는 체크박스
        Checkbox(
            checked = isCompleted,
            onCheckedChange = { /* 완료 상태 변경 로직 추가 예정 */ }
        )
        Spacer(Modifier.width(8.dp))

        // 할 일 이름 텍스트
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
    }
}

// Task 목록 전체를 스크롤 가능하게 표시하는 컴포넌트
@Composable
fun TaskList() {
    // 임시 Task 목록 데이터
    val dummyTasks = listOf(
        Pair("Task UI 레이아웃 완성하기", false),
        Pair("팀원 코드 검토하기", true),
        Pair("GitHub에 Push 하기", false)
    )

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        // 💡 수정된 부분: 'count ='를 제거하고 리스트를 직접 전달합니다.
        // 그리고 구조 분해를 사용하여 (name, isCompleted)로 바로 받습니다.
        items(dummyTasks) { (name, isCompleted) ->
            TaskItem(name = name, isCompleted = isCompleted)
            // 목록 항목 사이에 구분선 추가
            HorizontalDivider()
        }
    }
}