package com.apccarvalho.authapp.ui.theme.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apccarvalho.authapp.R
import com.apccarvalho.authapp.domain.AuthError
import com.apccarvalho.authapp.domain.AuthUser
import com.apccarvalho.authapp.domain.FieldError
import com.apccarvalho.authapp.ui.auth.AuthAction
import com.apccarvalho.authapp.ui.auth.AuthEvent
import com.apccarvalho.authapp.ui.auth.AuthField
import com.apccarvalho.authapp.ui.auth.AuthMode
import com.apccarvalho.authapp.ui.auth.AuthUiState
import com.apccarvalho.authapp.ui.auth.AuthViewModel
import com.apccarvalho.authapp.ui.components.AppTextField
import com.apccarvalho.authapp.ui.components.BrandMark
import com.apccarvalho.authapp.ui.components.ErrorBanner
import com.apccarvalho.authapp.ui.components.ModeToggle
import com.apccarvalho.authapp.ui.components.PasswordField
import com.apccarvalho.authapp.ui.components.PrimaryButton
import com.apccarvalho.authapp.ui.theme.AuthAppTheme
import com.apccarvalho.authapp.ui.theme.Sizes
import com.apccarvalho.authapp.ui.theme.Spacing

/**
 * Tela de autenticação ligada ao ViewModel.
 * @param onAuthenticated chamado uma única vez após login/cadastro com sucesso.
 * @param onForgotPassword recebe o e-mail já digitado, para pré-preencher a recuperação.
 */
@Composable
fun AuthScreen(
    onAuthenticated: (AuthUser) -> Unit,
    onForgotPassword: (email: String) -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnAuthenticated by rememberUpdatedState(onAuthenticated)
    val lifecycleOwner = LocalLifecycleOwner.current

    // Eventos são coletados só com a tela visível (STARTED).
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is AuthEvent.Authenticated -> currentOnAuthenticated(event.user)
                }
            }
        }
    }

    AuthContent(
        state = state,
        onAction = viewModel::onAction,
        onForgotPassword = onForgotPassword,
    )
}

/** Versão sem ViewModel: recebe estado e devolve ações. É a que aparece no Preview. */
@Composable
fun AuthContent(
    state: AuthUiState,
    onAction: (AuthAction) -> Unit,
    onForgotPassword: (email: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val enabled = !state.isLoading
    val moveDown = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
    val submit = {
        focusManager.clearFocus()
        onAction(AuthAction.Submit)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = Sizes.formMaxWidth).fillMaxWidth()) {
                BrandMark()
                Spacer(Modifier.height(Spacing.xl))

                AnimatedContent(
                    targetState = state.mode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "authHeader",
                ) { mode ->
                    AuthHeader(mode)
                }
                Spacer(Modifier.height(Spacing.lg))

                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(Spacing.lg)) {
                        ModeToggle(
                            mode = state.mode,
                            onModeChange = { onAction(AuthAction.ModeChanged(it)) },
                            enabled = enabled,
                        )
                        Spacer(Modifier.height(Spacing.lg))

                        // Nome — só no cadastro
                        AnimatedVisibility(
                            visible = state.isRegister,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                        ) {
                            Column {
                                AppTextField(
                                    value = state.name,
                                    onValueChange = { onAction(AuthAction.NameChanged(it)) },
                                    label = stringResource(R.string.field_name),
                                    error = state.nameError,
                                    enabled = enabled,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next,
                                    ),
                                    keyboardActions = moveDown,
                                    autofillType = ContentType.PersonFullName,
                                    onFocusLost = { onAction(AuthAction.FieldBlurred(AuthField.NAME)) },
                                )
                                Spacer(Modifier.height(Spacing.sm))
                            }
                        }

                        AppTextField(
                            value = state.email,
                            onValueChange = { onAction(AuthAction.EmailChanged(it)) },
                            label = stringResource(R.string.field_email),
                            error = state.emailError,
                            enabled = enabled,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                autoCorrectEnabled = false,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = moveDown,
                            autofillType = ContentType.EmailAddress,
                            onFocusLost = { onAction(AuthAction.FieldBlurred(AuthField.EMAIL)) },
                        )
                        Spacer(Modifier.height(Spacing.sm))

                        PasswordField(
                            value = state.password,
                            onValueChange = { onAction(AuthAction.PasswordChanged(it)) },
                            label = stringResource(R.string.field_password),
                            isVisible = state.isPasswordVisible,
                            onToggleVisibility = { onAction(AuthAction.TogglePasswordVisibility) },
                            error = state.passwordError,
                            hint = if (state.isRegister) stringResource(R.string.hint_password_register) else null,
                            enabled = enabled,
                            imeAction = if (state.isRegister) ImeAction.Next else ImeAction.Done,
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = { submit() },
                            ),
                            isNewPassword = state.isRegister,
                            onFocusLost = { onAction(AuthAction.FieldBlurred(AuthField.PASSWORD)) },
                        )

                        // Confirmação — só no cadastro
                        AnimatedVisibility(
                            visible = state.isRegister,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                        ) {
                            Column {
                                Spacer(Modifier.height(Spacing.sm))
                                PasswordField(
                                    value = state.confirmPassword,
                                    onValueChange = { onAction(AuthAction.ConfirmPasswordChanged(it)) },
                                    label = stringResource(R.string.field_confirm_password),
                                    isVisible = state.isPasswordVisible,
                                    onToggleVisibility = { onAction(AuthAction.TogglePasswordVisibility) },
                                    error = state.confirmPasswordError,
                                    enabled = enabled,
                                    imeAction = ImeAction.Done,
                                    keyboardActions = KeyboardActions(onDone = { submit() }),
                                    isNewPassword = true,
                                    onFocusLost = {
                                        onAction(AuthAction.FieldBlurred(AuthField.CONFIRM_PASSWORD))
                                    },
                                )
                            }
                        }

                        // "Esqueci minha senha" — só no login
                        AnimatedVisibility(visible = !state.isRegister) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    onClick = { onForgotPassword(state.email.trim()) },
                                    enabled = enabled,
                                ) {
                                    Text(stringResource(R.string.action_forgot_password))
                                }
                            }
                        }
                        Spacer(Modifier.height(Spacing.md))

                        ErrorBanner(error = state.formError)

                        PrimaryButton(
                            text = stringResource(
                                if (state.isRegister) R.string.action_register else R.string.action_login,
                            ),
                            onClick = submit,
                            isLoading = state.isLoading,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthHeader(mode: AuthMode) {
    val isLogin = mode == AuthMode.LOGIN
    Column {
        Text(
            text = stringResource(if (isLogin) R.string.auth_title_login else R.string.auth_title_register),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = stringResource(
                if (isLogin) R.string.auth_subtitle_login else R.string.auth_subtitle_register,
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

// ----------------------------- Previews -----------------------------

@Preview(name = "Login", showBackground = true, heightDp = 800)
@Composable
private fun LoginPreview() = AuthAppTheme {
    AuthContent(AuthUiState(email = "andreia@gmail.com"), {}, {})
}

@Preview(name = "Cadastro com erros", showBackground = true, heightDp = 900)
@Composable
private fun RegisterErrorPreview() = AuthAppTheme {
    AuthContent(
        AuthUiState(
            mode = AuthMode.REGISTER,
            name = "Andréia",
            email = "andreia@",
            password = "123",
            confirmPassword = "12",
            emailError = FieldError.INVALID_EMAIL,
            passwordError = FieldError.PASSWORD_TOO_SHORT,
            confirmPasswordError = FieldError.PASSWORDS_DONT_MATCH,
        ),
        {}, {},
    )
}

@Preview(name = "Carregando + erro do Firebase", showBackground = true, heightDp = 800)
@Composable
private fun LoadingPreview() = AuthAppTheme {
    AuthContent(
        AuthUiState(
            email = "andreia@gmail.com",
            password = "senha123",
            formError = AuthError.INVALID_CREDENTIALS,
            isLoading = true,
        ),
        {}, {},
    )
}
