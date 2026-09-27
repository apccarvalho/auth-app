package com.apccarvalho.authapp.ui.forgot

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.apccarvalho.authapp.data.AppContainer
import com.apccarvalho.authapp.data.AuthRepository
import com.apccarvalho.authapp.domain.AuthError
import com.apccarvalho.authapp.domain.AuthResult
import com.apccarvalho.authapp.domain.FieldError
import com.apccarvalho.authapp.domain.Validators
import com.apccarvalho.authapp.navigation.ForgotPasswordRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: FieldError? = null,
    val isLoading: Boolean = false,
    val formError: AuthError? = null,
    /** E-mail para o qual o último link foi enviado (null = ainda não enviou). */
    val sentTo: String? = null,
    /** Segundos até poder reenviar. 0 = liberado. */
    val cooldownSeconds: Int = 0,
) {
    val canSubmit: Boolean get() = !isLoading && cooldownSeconds == 0
}

class ForgotPasswordViewModel(
    private val repository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialEmail = savedStateHandle.toRoute<ForgotPasswordRoute>().email

    private val _uiState = MutableStateFlow(ForgotPasswordUiState(email = initialEmail))
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null

    fun onEmailChange(value: String) = _uiState.update {
        it.copy(
            email = value,
            emailError = it.emailError?.let { Validators.email(value) },
            formError = null,
        )
    }

    fun onEmailBlur() = _uiState.update {
        if (it.email.isBlank()) it else it.copy(emailError = Validators.email(it.email))
    }

    fun onSubmit() {
        val current = _uiState.value
        if (!current.canSubmit) return

        val error = Validators.email(current.email)
        if (error != null) {
            _uiState.update { it.copy(emailError = error) }
            return
        }

        _uiState.update { it.copy(isLoading = true, formError = null) }
        viewModelScope.launch {
            val email = current.email.trim()
            when (val result = repository.sendPasswordReset(email)) {
                is AuthResult.Success -> onSent(email)
                is AuthResult.Failure ->
                    // Se a proteção contra enumeração estiver DESLIGADA no projeto,
                    // o Firebase avisa quando o e-mail não existe. Tratamos igual a
                    // sucesso para não revelar quais e-mails têm conta.
                    if (result.error == AuthError.INVALID_CREDENTIALS) {
                        onSent(email)
                    } else {
                        _uiState.update { it.copy(isLoading = false, formError = result.error) }
                    }
            }
        }
    }

    private fun onSent(email: String) {
        _uiState.update { it.copy(isLoading = false, sentTo = email) }
        startCooldown()
    }

    /** Impede reenvios seguidos (e o bloqueio TOO_MANY_REQUESTS do Firebase). */
    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (remaining in COOLDOWN_SECONDS downTo 1) {
                _uiState.update { it.copy(cooldownSeconds = remaining) }
                delay(1_000)
            }
            _uiState.update { it.copy(cooldownSeconds = 0) }
        }
    }

    companion object {
        const val COOLDOWN_SECONDS = 60

        val Factory = viewModelFactory {
            initializer {
                ForgotPasswordViewModel(AppContainer.authRepository, createSavedStateHandle())
            }
        }
    }
}
