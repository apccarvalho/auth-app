package com.apccarvalho.authapp.data

import android.util.Log
import com.apccarvalho.authapp.domain.AuthError
import com.apccarvalho.authapp.domain.AuthErrorMapper
import com.apccarvalho.authapp.domain.AuthResult
import com.apccarvalho.authapp.domain.AuthUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
) : AuthRepository {

    init {
        // E-mails do Firebase (ex.: redefinição de senha) no idioma do aparelho.
        auth.useAppLanguage()
    }

    override val currentUser: AuthUser?
        get() = auth.currentUser?.toAuthUser()

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): AuthResult<AuthUser> =
        safeCall {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            result.user?.toAuthUser() ?: throw IllegalStateException("Usuário nulo após login")
        }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): AuthResult<AuthUser> = safeCall {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = result.user ?: throw IllegalStateException("Usuário nulo após cadastro")

        // A conta já existe neste ponto. Se salvar o nome falhar (ex.: rede caiu),
        // não tratamos como erro de cadastro: a saudação usa o e-mail como fallback.
        try {
            user.updateProfile(userProfileChangeRequest { displayName = name.trim() }).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Conta criada, mas não foi possível salvar o nome", e)
        }
        user.toAuthUser()
    }

    override suspend fun sendPasswordReset(email: String): AuthResult<Unit> = safeCall {
        auth.sendPasswordResetEmail(email.trim()).await()
        Unit
    }

    override fun signOut() = auth.signOut()

    /**
     * Executa a chamada ao Firebase e converte qualquer exceção em AuthResult.Failure.
     * CancellationException é relançada: engoli-la quebraria o cancelamento de
     * corrotinas (ex.: quando o ViewModel é destruído no meio da requisição).
     */
    private suspend fun <T> safeCall(block: suspend () -> T): AuthResult<T> =
        try {
            AuthResult.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val error = AuthErrorMapper.map(e)
            if (error == AuthError.UNKNOWN) Log.e(TAG, "Erro não mapeado", e)
            AuthResult.Failure(error)
        }

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName,
        createdAtMillis = metadata?.creationTimestamp,
    )

    private companion object {
        const val TAG = "FirebaseAuthRepository"
    }
}
