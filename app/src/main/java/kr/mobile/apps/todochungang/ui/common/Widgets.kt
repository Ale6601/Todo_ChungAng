package kr.mobile.apps.todochungang.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * 우측 상단 톱니바퀴 버튼 + Logout 메뉴
 *
 * - onLogout: Logout 메뉴를 눌렀을 때 호출되는 콜백
 *   (MainActivity 등에서 authViewModel.logout() 을 넘겨주면 됨)
 */
@Composable
fun AccountMenu(
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // 톱니바퀴 아이콘 버튼
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings"
        )
    }

    // 버튼을 눌렀을 때 펼쳐지는 드롭다운 메뉴
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(text = "Logout") },
            onClick = {
                expanded = false
                onLogout()
            }
        )
    }
}
