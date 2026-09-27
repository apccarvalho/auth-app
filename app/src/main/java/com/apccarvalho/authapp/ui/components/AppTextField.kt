package com.apccarvalho.authapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.apccarvalho.authapp.R
import com.apccarvalho.authapp.domain.FieldError
import com.apccarvalho.authapp.ui.common.messageRes

/**
 * Campo de texto padrão do app.
 * - mostra a mensagem do [error] abaixo do campo (ou a [hint], se não houver erro);
 * - avisa [onFocusLost] quando o usuário sai do campo;
 * - [autofillType] permite que gerenciadores de senha preencham o campo.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: FieldError? = null,
    hint: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    autofillType: ContentType? = null,
    onFocusLost: () -> Unit = {},
) {
    var hadFocus by remember { mutableStateOf(false) }
    val supporting: String? = error?.let { stringResource(it.messageRes()) } ?: hint

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focus ->
                if (hadFocus && !focus.isFocused) onFocusLost()
                hadFocus = focus.isFocused
            }
            .then(
                if (autofillType != null) Modifier.semantics { contentType = autofillType }
                else Modifier,
            ),
        enabled = enabled,
        label = { Text(label) },
        isError = error != null,
        supportingText = if (supporting != null) {
            { Text(supporting) }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    )
}

/** Campo de senha com botão de mostrar/ocultar. */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    error: FieldError? = null,
    hint: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isNewPassword: Boolean = false,
    onFocusLost: () -> Unit = {},
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        error = error,
        hint = hint,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrectEnabled = false,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        visualTransformation =
            if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility, enabled = enabled) {
                Icon(
                    painter = painterResource(
                        if (isVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility,
                    ),
                    contentDescription = stringResource(
                        if (isVisible) R.string.cd_hide_password else R.string.cd_show_password,
                    ),
                )
            }
        },
        autofillType = if (isNewPassword) ContentType.NewPassword else ContentType.Password,
        onFocusLost = onFocusLost,
    )
}
