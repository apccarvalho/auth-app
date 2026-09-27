package com.apccarvalho.authapp.data

import com.apccarvalho.authapp.domain.AuthResult
import com.apccarvalho.authapp.domain.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de autenticação.
 */
interface AuthRepository {

    /** Usuário logado agora (sessão persistida pelo Firebase), ou null. */
    val currentUser: AuthUser?

    /** Emite sempre que o usuário faz login ou logout. */
    fun observeAuthState(): Flow<AuthUser?>

    suspend fun signIn(email: String, password: String): AuthResult<AuthUser>

    suspend fun signUp(name: String, email: String, password: String): AuthResult<AuthUser>

    suspend fun sendPasswordReset(email: String): AuthResult<Unit>

    fun signOut()
}
