package kr.mobile.apps.todochungang.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AppScaffold(
    title: String,
    navController: NavHostController,
    onLogoutClick: () -> Unit,                   // 🔹 추가
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                )
                Divider()
            }
        },
        bottomBar = {
            BottomNavButtons(
                navController = navController,
                onLogoutClick = onLogoutClick     // ⬅ 여기서 전달
            )
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}
