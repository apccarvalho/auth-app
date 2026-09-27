package com.apccarvalho.authapp.ui.forgot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apccarvalho.authapp.R
import com.apccarvalho.authapp.ui.components.AppTextField
import com.apccarvalho.authapp.ui.components.ErrorBanner
import com.apccarvalho.authapp.ui.components.PrimaryButton
import com.apccarvalho.authapp.ui.theme.AuthAppTheme
import com.apccarvalho.authapp.ui.theme.Sizes
import com.apccarvalho.authapp.ui.theme.Spacing

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel(factory = ForgotPasswordViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ForgotPasswordContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onEmailBlur = viewModel::onEmailBlur,
        onSubmit = viewModel::onSubmit,
        onBack = onBack,
    )
}

@Composable
fun ForgotPasswordContent(
    state: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onEmailBlur: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val submit = {
        focusManager.clearFocus()
        onSubmit()
    }
    val buttonText = when {
        state.cooldownSeconds > 0 ->
            stringResource(R.string.forgot_resend_in, state.cooldownSeconds)
        state.sentTo != null -> stringResource(R.string.forgot_resend)
        else -> stringResource(R.string.forgot_send)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = Sizes.formMaxWidth)
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.cd_back),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(Modifier.height(Spacing.md))

            Text(
                text = stringResource(R.string.forgot_title),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.forgot_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(Modifier.height(Spacing.lg))

            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(Spacing.lg)) {
                    AppTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        label = stringResource(R.string.field_email),
                        error = state.emailError,
                        enabled = !state.isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Send,
                        ),
                        keyboardActions = KeyboardActions(onSend = { submit() }),
                        autofillType = ContentType.EmailAddress,
                        onFocusLost = onEmailBlur,
                    )
                    Spacer(Modifier.height(Spacing.md))

                    // Confirmação neutra: não diz se o e-mail tem ou não conta.
                    AnimatedVisibility(
                        visible = state.sentTo != null,
                        enter = fadeIn() + expandVertically(),
                    ) {
                        Column {
                            SentNotice(email = state.sentTo.orEmpty())
                            Spacer(Modifier.height(Spacing.md))
                        }
                    }

                    ErrorBanner(error = state.formError)

                    PrimaryButton(
                        text = buttonText,
                        onClick = submit,
                        isLoading = state.isLoading,
                        enabled = state.cooldownSeconds == 0,
                    )
                }
            }
            Spacer(Modifier.height(Spacing.sm))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.forgot_back_to_login))
                }
            }
        }
    }
}

@Composable
private fun SentNotice(email: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.md)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_mail),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(Spacing.sm))
            Text(
                text = stringResource(R.string.forgot_sent_notice, email),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

// ----------------------------- Previews -----------------------------

@Preview(name = "Recuperação — inicial", showBackground = true, heightDp = 700)
@Composable
private fun ForgotInitialPreview() = AuthAppTheme {
    ForgotPasswordContent(ForgotPasswordUiState(email = "andreia@gmail.com"), {}, {}, {}, {})
}

@Preview(name = "Recuperação — enviado", showBackground = true, heightDp = 700)
@Composable
private fun ForgotSentPreview() = AuthAppTheme {
    ForgotPasswordContent(
        ForgotPasswordUiState(
            email = "andreia@gmail.com",
            sentTo = "andreia@gmail.com",
            cooldownSeconds = 42,
        ),
        {}, {}, {}, {},
    )
}
