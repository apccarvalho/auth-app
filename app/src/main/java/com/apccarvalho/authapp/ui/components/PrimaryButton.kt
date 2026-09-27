package com.apccarvalho.authapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.apccarvalho.authapp.R
import com.apccarvalho.authapp.ui.theme.Sizes

/**
 * Botão principal (#56DDA7 com texto escuro).
 * Durante [isLoading] mostra um spinner no lugar do texto, mantém a cor de marca
 * (em vez do cinza de desabilitado) e ignora novos toques.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    val scheme = MaterialTheme.colorScheme
    val loadingLabel = stringResource(R.string.loading)

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(Sizes.buttonHeight)
            .semantics { if (isLoading) stateDescription = loadingLabel },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = scheme.primaryContainer,
            contentColor = scheme.onPrimaryContainer,
            disabledContainerColor =
                if (isLoading) scheme.primaryContainer else scheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor =
                if (isLoading) scheme.onPrimaryContainer else scheme.onSurface.copy(alpha = 0.38f),
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Sizes.inlineLoader),
                    color = scheme.onPrimaryContainer,
                    strokeWidth = Sizes.loaderStroke,
                )
            } else {
                Text(text, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
