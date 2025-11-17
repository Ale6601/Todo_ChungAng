package kr.mobile.apps.todochungang.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.mobile.apps.todochungang.data.repository.AuthRepository
import kr.mobile.apps.todochungang.utils.UiState
import com.google.android.gms.auth.api.signin.GoogleSignIn

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository(app.applicationContext)

    private val _loginState = MutableStateFlow<UiState<FirebaseUser>>(UiState.Loading)
    val loginState: StateFlow<UiState<FirebaseUser>> = _loginState

    init {
        // 앱 시작 시 이미 로그인된 유저가 있는지 체크
        val current = repoRunCurrentUser()
        if (current != null) {
            _loginState.value = UiState.Success(current)
        } else {
            _loginState.value = UiState.Error(IllegalStateException("Not logged in"))
        }
    }

    private fun repoRunCurrentUser(): FirebaseUser? =
        kr.mobile.apps.todochungang.firebase.FirebaseAuthHelper.currentUser

    fun getGoogleIntent() = repo.getGoogleIntent()

    fun handleGoogleActivityResult(task: Task<GoogleSignInAccount>) {
        val parsed = repo.parseGoogleResult(task)
        when (parsed) {
            is UiState.Success -> signInWithGoogle(parsed.data)
            is UiState.Error -> _loginState.value = UiState.Error(parsed.throwable)
            UiState.Loading -> Unit
        }
    }

    private fun signInWithGoogle(account: GoogleSignInAccount) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            _loginState.value = repo.signInWithGoogleAccount(account)
        }
    }

    fun signOut() {
        repo.signOut()
        _loginState.value = UiState.Error(IllegalStateException("Signed out"))
    }
}
