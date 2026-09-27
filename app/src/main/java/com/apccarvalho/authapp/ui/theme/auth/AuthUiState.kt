package com.apccarvalho.authapp.ui.auth

import com.apccarvalho.authapp.domain.AuthError
import com.apccarvalho.authapp.domain.AuthUser
import com.apccarvalho.authapp.domain.FieldError

enum class AuthMode { LOGIN, REGISTER }

enum class AuthField { NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

/** Tudo o que a tela precisa para se desenhar. Imutável: cada mudança gera uma cópia. */
data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: FieldError? = null,
    val emailError: FieldError? = null,
    val passwordError: FieldError? = null,
    val confirmPasswordError: FieldError? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    /** Erro vindo do Firebase, exibido no banner acima do botão. */
    val formError: AuthError? = null,
) {
    val isRegister: Boolean get() = mode == AuthMode.REGISTER

    val hasFieldErrors: Boolean
        get() = listOf(nameError, emailError, passwordError, confirmPasswordError)
            .any { it != null }
}

/** Ações que a tela envia ao ViewModel. */
sealed interface AuthAction {
    data class ModeChanged(val mode: AuthMode) : AuthAction
    data class NameChanged(val value: String) : AuthAction
    data class EmailChanged(val value: String) : AuthAction
    data class PasswordChanged(val value: String) : AuthAction
    data class ConfirmPasswordChanged(val value: String) : AuthAction
    data class FieldBlurred(val field: AuthField) : AuthAction
    data object TogglePasswordVisibility : AuthAction
    data object Submit : AuthAction
}

/** Eventos únicos (acontecem uma vez), separados do estado para não repetirem na rotação. */
sealed interface AuthEvent {
    data class Authenticated(val user: AuthUser) : AuthEvent
}
