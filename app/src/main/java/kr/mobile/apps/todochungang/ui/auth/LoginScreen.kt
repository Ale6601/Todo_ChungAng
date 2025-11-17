package kr.mobile.apps.todochungang.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kr.mobile.apps.todochungang.utils.UiState

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()

    // Google Sign-In 결과를 받는 launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        viewModel.handleGoogleActivityResult(task)
    }

    // 로그인 성공 시 콜백 호출 → Main 화면으로 이동
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            onLoginSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Todo ChungAng",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { launcher.launch(viewModel.getGoogleIntent()) }
                ) {
                    Text("Google 계정으로 계속하기")
                }
                Spacer(Modifier.height(16.dp))

                when (loginState) {
                    UiState.Loading -> CircularProgressIndicator()
                    is UiState.Error -> Text(
                        text = (loginState as UiState.Error).throwable.localizedMessage
                            ?: "로그인 실패",
                        color = MaterialTheme.colorScheme.error
                    )
                    is UiState.Success -> {
                        Text("로그인 완료, 화면 전환 중…")
                    }
                }
            }
        }
    }
}
