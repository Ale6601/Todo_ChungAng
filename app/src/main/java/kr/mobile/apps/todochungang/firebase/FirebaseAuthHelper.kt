package kr.mobile.apps.todochungang.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthHelper {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * 새 이름: logout()
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * 기존 이름: signOut()
     * - 혹시 다른 코드에서 이미 쓰고 있을 수 있으니,
     *   내부적으로 logout()을 호출하게만 유지
     */
    fun signOut() = logout()
}
