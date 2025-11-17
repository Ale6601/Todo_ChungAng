package kr.mobile.apps.todochungang.data.source

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kr.mobile.apps.todochungang.R
import kr.mobile.apps.todochungang.firebase.FirebaseAuthHelper

class AuthSource(private val context: Context) {

    // google-services.json에서 자동으로 생성된 default_web_client_id 사용
    private val webClientId: String by lazy {
        context.getString(R.string.default_web_client_id)
    }

    private val signInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)   // ⬅️ Firebase Auth 연동용
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent() = signInClient.signInIntent

    @Throws(ApiException::class)
    fun getAccountFromResult(task: Task<GoogleSignInAccount>): GoogleSignInAccount {
        return task.getResult(ApiException::class.java)
    }

    suspend fun signInWithFirebase(account: GoogleSignInAccount): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val result = FirebaseAuthHelper.auth.signInWithCredential(credential).await()
        return result.user
    }

    fun signOut() {
        signInClient.signOut()
        FirebaseAuthHelper.signOut()
    }
}
