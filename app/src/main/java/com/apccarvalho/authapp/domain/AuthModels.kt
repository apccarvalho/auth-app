package com.apccarvalho.authapp.domain

/** Usuário autenticado, independente do Firebase (a UI nunca vê FirebaseUser). */
data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String?,
    val createdAtMillis: Long?,
) {
    /** Nome para a saudação: displayName ou, na falta dele, a parte do e-mail antes do "@". */
    val greetingName: String
        get() = displayName?.takeIf { it.isNotBlank() }?.trim()
            ?: email.substringBefore("@")
}

/** Resultado de toda operação de autenticação. */
sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Failure(val error: AuthError) : AuthResult<Nothing>
}

/** Erros que a UI sabe explicar ao usuário. */
enum class AuthError {
    EMAIL_ALREADY_IN_USE,
    WEAK_PASSWORD,
    INVALID_EMAIL,
    /** E-mail inexistente OU senha errada — deliberadamente indistinguíveis. */
    INVALID_CREDENTIALS,
    USER_DISABLED,
    TOO_MANY_REQUESTS,
    NETWORK,
    /** Provedor E-mail/senha desativado no Firebase Console. */
    OPERATION_NOT_ALLOWED,
    UNKNOWN,
}
