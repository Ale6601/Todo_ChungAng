package kr.mobile.apps.todochungang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kr.mobile.apps.todochungang.ui.calendar.CalendarNavigator
import kr.mobile.apps.todochungang.ui.calendar.sampleEventsForMonth
import kr.mobile.apps.todochungang.ui.components.BottomNavButtons
import kr.mobile.apps.todochungang.ui.tasks.TasksScreen
import kr.mobile.apps.todochungang.ui.theme.TodoChungAngTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoChungAngTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "calendar") {
                    composable("calendar") {
                        Scaffold(
                            bottomBar = { BottomNavButtons(navController) }
                        ) { innerPadding ->
                            Modifier.padding(innerPadding)
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
                                contentAlignment = androidx.compose.ui.Alignment.Center
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
