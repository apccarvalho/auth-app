package com.apccarvalho.authapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.apccarvalho.authapp.domain.AuthError
import com.apccarvalho.authapp.ui.common.messageRes
import com.apccarvalho.authapp.ui.theme.Spacing

/**
 * Mensagem de erro do Firebase, acima do botão. Fica visível até a pessoa
 * editar algum campo. O liveRegion faz o TalkBack ler a mensagem ao aparecer.
 */
@Composable
fun ErrorBanner(error: AuthError?, modifier: Modifier = Modifier) {
    // Guarda o último erro para o texto não sumir durante a animação de saída.
    var lastError by remember { mutableStateOf(error) }
    if (error != null) lastError = error

    AnimatedVisibility(
        visible = error != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier,
    ) {
        Column {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                lastError?.let {
                    Text(
                        text = stringResource(it.messageRes()),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                            .semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.md))
        }
    }
}
