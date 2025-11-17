package kr.mobile.apps.todochungang.data.repository

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.common.api.ApiException
import kr.mobile.apps.todochungang.data.source.AuthSource
import kr.mobile.apps.todochungang.utils.UiState

class AuthRepository(context: Context) {

    private val source = AuthSource(context)

    fun getGoogleIntent(): Intent = source.getGoogleSignInIntent()

    fun parseGoogleResult(task: Task<GoogleSignInAccount>): UiState<GoogleSignInAccount> =
        try {
            UiState.Success(source.getAccountFromResult(task))
        } catch (e: ApiException) {
            UiState.Error(e)
        }

    suspend fun signInWithGoogleAccount(
        account: GoogleSignInAccount
    ): UiState<FirebaseUser> = try {
        val user = source.signInWithFirebase(account)
        if (user != null) UiState.Success(user)
        else UiState.Error(IllegalStateException("User is null"))
    } catch (t: Throwable) {
        UiState.Error(t)
    }

    fun signOut() {
        source.signOut()
    }
}
