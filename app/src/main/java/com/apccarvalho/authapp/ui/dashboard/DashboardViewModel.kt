package com.apccarvalho.authapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apccarvalho.authapp.data.AppContainer
import com.apccarvalho.authapp.data.AuthRepository
import com.apccarvalho.authapp.domain.AuthUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    /** null = sessão encerrada → a tela navega para o login. */
    val user: AuthUser?,
    val showSignOutDialog: Boolean = false,
)

/*
 * O Dashboard OBSERVA o estado de autenticação em vez de apenas ler o usuário
 * uma vez. Assim, qualquer motivo de fim de sessão (logout pelo botão, conta
 * desativada, token revogado) leva o usuário de volta ao login pelo mesmo caminho.
 */
class DashboardViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val showSignOutDialog = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> =
        combine(repository.observeAuthState(), showSignOutDialog) { user, showDialog ->
            DashboardUiState(user = user, showSignOutDialog = showDialog)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState(user = repository.currentUser),
        )

    fun onSignOutClick() {
        showSignOutDialog.value = true
    }

    fun onSignOutDismiss() {
        showSignOutDialog.value = false
    }

    fun onSignOutConfirm() {
        showSignOutDialog.value = false
        // O listener do Firebase emite null → uiState.user vira null → a tela navega.
        repository.signOut()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { DashboardViewModel(AppContainer.authRepository) }
        }
    }
}
