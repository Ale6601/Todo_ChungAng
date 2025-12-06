package kr.mobile.apps.todochungang.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavButtons(
    navController: NavController,
    onLogoutClick: () -> Unit      // 🔹 추가: 로그아웃 콜백
) {
    // Settings 드롭다운 상태
    val (settingsExpanded, setSettingsExpanded) = remember { mutableStateOf(false) }

    // 현재 route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedColor = Color.Black
    val unselectedColor = Color(0xFF9CA3AF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Calendar
        BottomNavItem(
            label = "Calendar",
            icon = Icons.Filled.CalendarMonth,
            selected = currentRoute == "calendar",
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            onClick = { navController.navigate("calendar") },
            modifier = Modifier.weight(1f)
        )

        // Tasks
        BottomNavItem(
            label = "Tasks",
            icon = Icons.Filled.TaskAlt,
            selected = currentRoute == "tasks",
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            onClick = { navController.navigate("tasks") },
            modifier = Modifier.weight(1f)
        )

        // Settings + Dropdown
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            BottomNavItem(
                label = "Settings",
                icon = Icons.Filled.Settings,
                // profile 화면을 Settings 탭으로 간주
                selected = currentRoute == "profile",
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = { setSettingsExpanded(!settingsExpanded) }
            )

            DropdownMenu(
                expanded = settingsExpanded,
                onDismissRequest = { setSettingsExpanded(false) },
                modifier = Modifier.width(160.dp),
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                // 상단 헤더
                Text(
                    text = "My Account",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Divider()

                // Profile
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    text = { Text("Profile") },
                    onClick = {
                        setSettingsExpanded(false)
                        navController.navigate("profile")
                    }
                )

                // Settings
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    text = { Text("Settings") },
                    onClick = {
                        setSettingsExpanded(false)
                        // TODO: Settings 화면 이동 or 다이얼로그
                    }
                )

                Divider()

                // 🔥 Logout
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Logout"
                        )
                    },
                    text = { Text("Logout") },
                    onClick = {
                        setSettingsExpanded(false)
                        onLogoutClick()        // ⬅ 여기서만 콜백 호출
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) selectedColor else unselectedColor

    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = tint,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
