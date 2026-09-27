package com.apccarvalho.authapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apccarvalho.authapp.data.AppContainer
import com.apccarvalho.authapp.data.AuthRepository
import com.apccarvalho.authapp.domain.AuthResult
import com.apccarvalho.authapp.domain.FieldError
import com.apccarvalho.authapp.domain.Validators
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
 * Regra de exibição de erros de campo:
 *  - enquanto a pessoa digita pela primeira vez, nenhum erro aparece;
 *  - ao sair do campo (blur) ou ao tocar no botão, o campo é validado;
 *  - se o campo já mostra um erro, ele é revalidado a cada tecla,
 *    para o erro sumir assim que for corrigido.
 */
class AuthViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events: Flow<AuthEvent> = _events.receiveAsFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.ModeChanged -> changeMode(action.mode)
            is AuthAction.NameChanged -> _uiState.update {
                it.copy(
                    name = action.value,
                    nameError = it.nameError?.let { Validators.name(action.value) },
                    formError = null,
                )
            }
            is AuthAction.EmailChanged -> _uiState.update {
                it.copy(
                    email = action.value,
                    emailError = it.emailError?.let { Validators.email(action.value) },
                    formError = null,
                )
            }
            is AuthAction.PasswordChanged -> _uiState.update {
                it.copy(
                    password = action.value,
                    passwordError = it.passwordError?.let { _ -> passwordRule(it.mode, action.value) },
                    // A confirmação depende da senha: revalida se já foi digitada.
                    confirmPasswordError = if (it.isRegister && it.confirmPassword.isNotEmpty()) {
                        Validators.confirmPassword(action.value, it.confirmPassword)
                    } else {
                        it.confirmPasswordError
                    },
                    formError = null,
                )
            }
            is AuthAction.ConfirmPasswordChanged -> _uiState.update {
                it.copy(
                    confirmPassword = action.value,
                    // Confirmação é validada em tempo real assim que algo é digitado.
                    confirmPasswordError =
                        if (action.value.isNotEmpty() || it.confirmPasswordError != null) {
                            Validators.confirmPassword(it.password, action.value)
                        } else {
                            null
                        },
                    formError = null,
                )
            }
            is AuthAction.FieldBlurred -> validateOnBlur(action.field)
            AuthAction.TogglePasswordVisibility -> _uiState.update {
                it.copy(isPasswordVisible = !it.isPasswordVisible)
            }
            AuthAction.Submit -> submit()
        }
    }

    private fun changeMode(mode: AuthMode) = _uiState.update {
        if (it.isLoading || it.mode == mode) {
            it
        } else {
            // Mantém o que foi digitado; limpa só os erros do modo anterior.
            it.copy(
                mode = mode,
                nameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                formError = null,
            )
        }
    }

    /** Ao sair de um campo vazio não mostramos erro — só se a pessoa tentar enviar. */
    private fun validateOnBlur(field: AuthField) = _uiState.update { s ->
        when (field) {
            AuthField.NAME ->
                if (s.name.isBlank()) s else s.copy(nameError = Validators.name(s.name))
            AuthField.EMAIL ->
                if (s.email.isBlank()) s else s.copy(emailError = Validators.email(s.email))
            AuthField.PASSWORD ->
                if (s.password.isEmpty()) s
                else s.copy(passwordError = passwordRule(s.mode, s.password))
            AuthField.CONFIRM_PASSWORD ->
                if (s.confirmPassword.isEmpty()) s
                else s.copy(
                    confirmPasswordError = Validators.confirmPassword(s.password, s.confirmPassword),
                )
        }
    }

    private fun submit() {
        val current = _uiState.value
        if (current.isLoading) return // evita duplo clique

        val validated = validateAll(current)
        if (validated.hasFieldErrors) {
            _uiState.value = validated
            return
        }

        _uiState.value = validated.copy(isLoading = true, formError = null)

        viewModelScope.launch {
            val result = when (validated.mode) {
                AuthMode.LOGIN -> repository.signIn(validated.email, validated.password)
                AuthMode.REGISTER -> repository.signUp(
                    name = validated.name,
                    email = validated.email,
                    password = validated.password,
                )
            }
            when (result) {
                is AuthResult.Success -> {
                    // Senhas não ficam na memória depois do login.
                    _uiState.update {
                        it.copy(isLoading = false, password = "", confirmPassword = "")
                    }
                    _events.send(AuthEvent.Authenticated(result.data))
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, formError = result.error)
                }
            }
        }
    }

    private fun validateAll(s: AuthUiState): AuthUiState = s.copy(
        nameError = if (s.isRegister) Validators.name(s.name) else null,
        emailError = Validators.email(s.email),
        passwordError = passwordRule(s.mode, s.password),
        confirmPasswordError =
            if (s.isRegister) Validators.confirmPassword(s.password, s.confirmPassword) else null,
    )

    /** No login só exigimos preenchimento; no cadastro, o mínimo do Firebase. */
    private fun passwordRule(mode: AuthMode, value: String): FieldError? = when (mode) {
        AuthMode.LOGIN -> Validators.loginPassword(value)
        AuthMode.REGISTER -> Validators.newPassword(value)
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { AuthViewModel(AppContainer.authRepository) }
        }
    }
}
