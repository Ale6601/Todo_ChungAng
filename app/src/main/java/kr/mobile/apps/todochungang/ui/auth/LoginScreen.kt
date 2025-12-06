package kr.mobile.apps.todochungang.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kr.mobile.apps.todochungang.utils.UiState

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()

    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        viewModel.handleGoogleActivityResult(task)
    }

    // Success → main 화면 이동
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            onLoginSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // 앱 제목 (검정, 두껍게)
                Text(
                    text = "Todo ChungAng",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                )

                Spacer(Modifier.height(16.dp))

                // 캘린더 아이콘 (검정)
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(Modifier.height(32.dp))

                // 🔥 Google 로그인 버튼 (흰/검 스타일)
                OutlinedButton(
                    onClick = { launcher.launch(viewModel.getGoogleIntent()) },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Sign in with Google",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.Black)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 🔥 상태 메시지
                when (loginState) {
                    UiState.Loading -> CircularProgressIndicator(color = Color.Black)

                    is UiState.Error -> Text(
                        text = (loginState as UiState.Error).throwable.localizedMessage
                            ?: "Failed",
                        color = Color(0xFFB00020), // error 색도 붉은톤
                        style = MaterialTheme.typography.bodySmall
                    )

                    is UiState.Success -> Text(
                        "Success",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
