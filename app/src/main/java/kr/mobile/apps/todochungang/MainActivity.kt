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
import androidx.compose.ui.unit.dp
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
import kr.mobile.apps.todochungang.ui.common.AccountMenu
import kr.mobile.apps.todochungang.ui.profile.ProfileScreen
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
                            Scaffold(
                                // 🔥 TopAppBar 없이 actions 만 직접 배치
                                topBar = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 16.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        AccountMenu(
                                            onLogout = { authViewModel.logout() }
                                        )
                                    }
                                },
                                bottomBar = { BottomNavButtons(navController) }
                            ) { innerPadding ->
                                Modifier
                                    .padding(innerPadding)
                                    .CalendarNavigator { sampleEventsForMonth(it) }
                            }
                        }

                        // 🔹 Tasks 화면
                        composable("tasks") {
                            Scaffold(
                                topBar = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        AccountMenu(
                                            onLogout = { authViewModel.logout() }
                                        )
                                    }
                                },
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


                        composable("profile") {
                            Scaffold(
                                bottomBar = { BottomNavButtons(navController) }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    ProfileScreen()
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
