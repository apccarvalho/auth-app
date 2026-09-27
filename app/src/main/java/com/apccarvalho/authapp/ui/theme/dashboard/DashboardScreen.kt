package com.apccarvalho.authapp.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apccarvalho.authapp.R
import com.apccarvalho.authapp.domain.AuthUser
import com.apccarvalho.authapp.ui.components.BrandMark
import com.apccarvalho.authapp.ui.theme.AuthAppTheme
import com.apccarvalho.authapp.ui.theme.Sizes
import com.apccarvalho.authapp.ui.theme.Spacing
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onSignedOut: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnSignedOut by rememberUpdatedState(onSignedOut)
    val isSignedOut = state.user == null

    LaunchedEffect(isSignedOut) {
        if (isSignedOut) currentOnSignedOut()
    }

    // Enquanto navega para o login, não há usuário para exibir.
    val user = state.user ?: return

    DashboardContent(
        user = user,
        showSignOutDialog = state.showSignOutDialog,
        onSignOutClick = viewModel::onSignOutClick,
        onSignOutDismiss = viewModel::onSignOutDismiss,
        onSignOutConfirm = viewModel::onSignOutConfirm,
    )
}

@Composable
fun DashboardContent(
    user: AuthUser,
    showSignOutDialog: Boolean,
    onSignOutClick: () -> Unit,
    onSignOutDismiss: () -> Unit,
    onSignOutConfirm: () -> Unit,
) {
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
        ) {
            BrandMark()
            Spacer(Modifier.height(Spacing.xxl))

            Avatar(user.greetingName)
            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = stringResource(R.string.dashboard_greeting, user.greetingName),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.dashboard_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(Modifier.height(Spacing.xl))

            AccountCard(user)

            Spacer(Modifier.height(Spacing.xl))

            OutlinedButton(
                onClick = onSignOutClick,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth().height(Sizes.buttonHeight),
            ) {
                Text(stringResource(R.string.dashboard_sign_out), style = MaterialTheme.typography.labelLarge)
            }
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = onSignOutDismiss,
            title = { Text(stringResource(R.string.sign_out_dialog_title)) },
            text = { Text(stringResource(R.string.sign_out_dialog_text)) },
            confirmButton = {
                TextButton(onClick = onSignOutConfirm) {
                    Text(stringResource(R.string.sign_out_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onSignOutDismiss) {
                    Text(stringResource(R.string.sign_out_dialog_cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            shape = MaterialTheme.shapes.extraLarge,
        )
    }
}

/** Círculo com a inicial do nome, na cor de marca. */
@Composable
private fun Avatar(name: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun AccountCard(user: AuthUser) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(Spacing.lg)) {
            Text(
                text = stringResource(R.string.dashboard_account_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(Spacing.md))
            InfoRow(stringResource(R.string.dashboard_email_label), user.email)

            user.createdAtMillis?.let { millis ->
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Spacing.sm),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                InfoRow(stringResource(R.string.dashboard_created_label), formatDate(millis))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Ex.: 27 de setembro de 2026 */
private fun formatDate(millis: Long): String =
    DateFormat.getDateInstance(DateFormat.LONG, Locale.forLanguageTag("pt-BR")).format(Date(millis))

// ----------------------------- Previews -----------------------------

private val previewUser = AuthUser(
    uid = "preview",
    email = "andreia@gmail.com",
    displayName = "Andréia",
    createdAtMillis = 1_790_000_000_000,
)

@Preview(name = "Dashboard", showBackground = true, heightDp = 800)
@Composable
private fun DashboardPreview() = AuthAppTheme {
    DashboardContent(previewUser, showSignOutDialog = false, {}, {}, {})
}

@Preview(name = "Confirmação de saída", showBackground = true, heightDp = 800)
@Composable
private fun SignOutDialogPreview() = AuthAppTheme {
    DashboardContent(previewUser, showSignOutDialog = true, {}, {}, {})
}
