package com.apccarvalho.authapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.apccarvalho.authapp.R

/*
 * Figtree (licença SIL OFL, arquivo em res/font/figtree.ttf).
 * É uma fonte variável: um único arquivo com todos os pesos. Cada Font abaixo
 * aponta para o mesmo arquivo com um eixo de peso diferente (requer minSdk 26).
 */
@OptIn(ExperimentalTextApi::class)
private fun figtree(weight: FontWeight) = Font(
    resId = R.font.figtree,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val Figtree = FontFamily(
    figtree(FontWeight.Normal),
    figtree(FontWeight.Medium),
    figtree(FontWeight.SemiBold),
    figtree(FontWeight.Bold),
    figtree(FontWeight.ExtraBold),
)

// Escala enxuta: só os estilos que o app realmente usa têm ajuste fino.
val AppTypography = Typography(
    // Título grande da tela de login / saudação do dashboard
    displaySmall = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.Bold,
        fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.25).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
    // Texto de botões
    labelLarge = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Figtree, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp,
    ),
)
