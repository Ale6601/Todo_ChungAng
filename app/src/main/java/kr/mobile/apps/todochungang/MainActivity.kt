package kr.mobile.apps.todochungang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import kr.mobile.apps.todochungang.ui.auth.AuthViewModel
import kr.mobile.apps.todochungang.ui.auth.LoginScreen
import kr.mobile.apps.todochungang.ui.calendar.CalendarNavigator
import kr.mobile.apps.todochungang.ui.calendar.sampleEventsForMonth
import kr.mobile.apps.todochungang.ui.common.BottomNavButtons
import kr.mobile.apps.todochungang.ui.tasks.TasksScreen
import kr.mobile.apps.todochungang.ui.theme.TodoChungAngTheme
import kr.mobile.apps.todochungang.utils.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoChungAngTheme {

                // 🔹 AuthViewModel 가져오고 로그인 상태 관찰
                val authViewModel: AuthViewModel = viewModel()
                val loginState by authViewModel.loginState.collectAsState()

                var isLoggedIn by remember {
                    mutableStateOf(loginState is UiState.Success<FirebaseUser>)
                }

                // loginState 변할 때마다 isLoggedIn 갱신
                LaunchedEffect(loginState) {
                    isLoggedIn = loginState is UiState.Success<FirebaseUser>
                }

                if (!isLoggedIn) {
                    // 🔹 아직 로그인 안 됐으면 LoginScreen만 보여줌
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = { isLoggedIn = true }
                    )
                } else {
                    // 🔹 로그인 완료된 상태 → 기존 NavHost + BottomNav 표시
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "calendar"
                    ) {
                        composable("calendar") {
                            Scaffold(
                                bottomBar = { BottomNavButtons(navController) }
                            ) { innerPadding ->
                                Modifier
                                    .padding(innerPadding)
                                    .CalendarNavigator { sampleEventsForMonth(it) }
                            }
                        }

                        composable("tasks") {
                            Scaffold(
                                bottomBar = { BottomNavButtons(navController) }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TasksScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
