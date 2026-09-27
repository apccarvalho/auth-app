package com.apccarvalho.authapp.domain

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Converte exceções do Firebase em [AuthError].
 *
 * A ORDEM dos casos importa: FirebaseAuthWeakPasswordException é subclasse de
 * FirebaseAuthInvalidCredentialsException.
 */
object AuthErrorMapper {

    fun map(throwable: Throwable): AuthError = when (throwable) {
        is FirebaseAuthWeakPasswordException -> AuthError.WEAK_PASSWORD
        is FirebaseAuthUserCollisionException -> AuthError.EMAIL_ALREADY_IN_USE
        is FirebaseAuthInvalidCredentialsException ->
            if (throwable.errorCode == "ERROR_INVALID_EMAIL") AuthError.INVALID_EMAIL
            else AuthError.INVALID_CREDENTIALS
        is FirebaseAuthInvalidUserException ->
            if (throwable.errorCode == "ERROR_USER_DISABLED") AuthError.USER_DISABLED
            // USER_NOT_FOUND vira "credenciais inválidas" de propósito:
            // não revelamos quais e-mails têm conta.
            else AuthError.INVALID_CREDENTIALS
        is FirebaseNetworkException -> AuthError.NETWORK
        is FirebaseTooManyRequestsException -> AuthError.TOO_MANY_REQUESTS
        is FirebaseAuthException -> when (throwable.errorCode) {
            "ERROR_OPERATION_NOT_ALLOWED" -> AuthError.OPERATION_NOT_ALLOWED
            "ERROR_TOO_MANY_REQUESTS" -> AuthError.TOO_MANY_REQUESTS
            else -> AuthError.UNKNOWN
        }
        else -> AuthError.UNKNOWN
    }
}
