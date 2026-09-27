package com.apccarvalho.authapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/*
 * Raios diferentes por hierarquia, em vez de um valor único para tudo:
 * chips e indicadores pequenos → 8dp; campos e botões → 14dp;
 * cartão do formulário e bottom sheets → 28dp.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
