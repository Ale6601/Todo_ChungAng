package kr.mobile.apps.todochungang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import kr.mobile.apps.todochungang.ui.auth.AuthViewModel
import kr.mobile.apps.todochungang.ui.auth.LoginScreen
import kr.mobile.apps.todochungang.ui.calendar.CalendarNavigator
import kr.mobile.apps.todochungang.ui.calendar.WeekStart
import kr.mobile.apps.todochungang.ui.common.AppScaffold
import kr.mobile.apps.todochungang.ui.profile.ProfileScreen
import kr.mobile.apps.todochungang.ui.settings.SettingsScreen
import kr.mobile.apps.todochungang.ui.tasks.TasksScreen
import kr.mobile.apps.todochungang.ui.theme.TodoChungAngTheme
import kr.mobile.apps.todochungang.utils.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TodoChungAngTheme {

                val authViewModel: AuthViewModel = viewModel()
                val loginState by authViewModel.loginState.collectAsState()
                var weekStart by remember { mutableStateOf(WeekStart.MONDAY) }
                var calendarBackgroundColor by remember { mutableStateOf(Color.White) }

                var isLoggedIn by remember {
                    mutableStateOf(loginState is UiState.Success<FirebaseUser>)
                }

                LaunchedEffect(loginState) {
                    isLoggedIn = loginState is UiState.Success<FirebaseUser>
                }

                if (!isLoggedIn) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = { isLoggedIn = true }
                    )
                } else {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "calendar"
                    ) {
                        // 🔹 Calendar 화면
                        composable("calendar") {
                            AppScaffold(
                                title = "Calendar",
                                navController = navController,
                                onLogoutClick = { authViewModel.logout() }
                            ) { modifier ->
                                CalendarNavigator(
                                    modifier = modifier,
                                    weekStart = weekStart,
                                    backgroundColor = calendarBackgroundColor
                                )
                            }
                        }

                        // 🔹 Tasks 화면
                        composable("tasks") {
                            AppScaffold(
                                title = "Tasks",
                                navController = navController,
                                onLogoutClick = { authViewModel.logout() }
                            ) { modifier ->
                                TasksScreen(
                                    modifier = modifier
                                )
                            }
                        }

                        // 🔹 Settings/Profile 화면
                        composable("profile") {
                            AppScaffold(
                                title = "Profile",
                                navController = navController,
                                onLogoutClick = { authViewModel.logout() }
                            ) { modifier ->
                                Box(
                                    modifier = modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    ProfileScreen()
                                }
                            }
                        }

                        composable("settings") {
                            AppScaffold(
                                title = "Settings",
                                navController = navController,
                                onLogoutClick = { authViewModel.logout() }   // 🔥 여기
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    SettingsScreen(
                                        weekStart = weekStart,
                                        onWeekStartChange = { weekStart = it },
                                        backgroundColor = calendarBackgroundColor,
                                        onBackgroundColorChange = { calendarBackgroundColor = it }
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
