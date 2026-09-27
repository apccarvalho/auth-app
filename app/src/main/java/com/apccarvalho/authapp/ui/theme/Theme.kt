package com.apccarvalho.authapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*
 * Mapeamento da paleta para os papéis do Material 3.
 */
private val MintLightColors = lightColorScheme(
    primary = MintDeep,
    onPrimary = White,
    primaryContainer = Mint400,
    onPrimaryContainer = Ink900,
    inversePrimary = Mint300,

    secondary = Ink700,
    onSecondary = White,
    secondaryContainer = Mint200,
    onSecondaryContainer = Ink900,

    tertiary = Mint300,
    onTertiary = Ink900,
    tertiaryContainer = Mint100,
    onTertiaryContainer = Ink900,

    background = Mint50,
    onBackground = Ink900,

    surface = Mint50,
    onSurface = Ink900,
    surfaceVariant = Mint100,
    onSurfaceVariant = Ink600,
    surfaceTint = Mint400,

    surfaceContainerLowest = White,
    surfaceContainerLow = Mint50,
    surfaceContainer = Mint100,
    surfaceContainerHigh = Mint100,
    surfaceContainerHighest = Mint200,

    outline = Ink400,
    outlineVariant = Mint200,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    inverseSurface = Ink900,
    inverseOnSurface = Mint50,
    scrim = Ink900,
)

/**
 * Tema do app. Sempre claro e sem dynamic color
 */
@Composable
fun AuthAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MintLightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}