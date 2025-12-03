package kr.mobile.apps.todochungang.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavButtons(navController: NavController) {
    // 🔹 State für das Dropdown
    val (settingsExpanded, setSettingsExpanded) = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ---- Calendar button ----
        Button(
            onClick = { navController.navigate("calendar") },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text("Calendar", style = MaterialTheme.typography.labelLarge)
            }
        }

        // ---- Tasks button ----
        Button(
            onClick = { navController.navigate("tasks") },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.TaskAlt,
                    contentDescription = "Tasks",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text("Tasks", style = MaterialTheme.typography.labelLarge)
            }
        }

        // ---- Settings mit Dropdown ----
        Box {
            Button(
                onClick = { setSettingsExpanded(!settingsExpanded) },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text("Settings", style = MaterialTheme.typography.labelLarge)
                }
            }

            DropdownMenu(
                expanded = settingsExpanded,
                onDismissRequest = { setSettingsExpanded(false) }
            ) {
                DropdownMenuItem(
                    text = { Text("Profile") },
                    onClick = {
                        setSettingsExpanded(false)
                        navController.navigate("profile")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        setSettingsExpanded(false)
                        // TODO: Aktion für Option 2
                    }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        setSettingsExpanded(false)
                        // TODO: Aktion für Option 3
                    }
                )
            }
        }
    }
}
