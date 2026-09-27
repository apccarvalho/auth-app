package com.apccarvalho.authapp.data

/**
 * Injeção de dependência manual: um único repositório para o app inteiro.
 */
object AppContainer {
    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
}
