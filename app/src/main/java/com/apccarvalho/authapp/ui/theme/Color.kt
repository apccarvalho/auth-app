package com.apccarvalho.authapp.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Paleta base (definida na atividade) — do mais claro ao mais intenso
// ---------------------------------------------------------------------------
val Mint50 = Color(0xFFEBFBF4)   // fundo das telas
val Mint100 = Color(0xFFD5F7E9)  // cartões, superfícies elevadas
val Mint200 = Color(0xFFABEED3)  // divisores, trilho do medidor de força
val Mint300 = Color(0xFF80E6BD)  // destaques secundários
val Mint400 = Color(0xFF56DDA7)  // cor de marca: botão principal

// ---------------------------------------------------------------------------
// Tons de apoio (fora da paleta, derivados do mesmo matiz verde)
// Necessários porque texto branco sobre #56DDA7 tem contraste 1,7:1 e o
// próprio #56DDA7 sobre o fundo tem 1,6:1 — ilegível para textos e bordas.
// Contrastes medidos sobre Mint50 (WCAG AA pede 4,5:1 para texto, 3:1 para bordas).
// ---------------------------------------------------------------------------
val Ink900 = Color(0xFF0E3326)   // texto principal — 12,9:1 no fundo, 8,1:1 no Mint400
val Ink700 = Color(0xFF2F5A4A)   // texto de apoio — 7,3:1
val Ink600 = Color(0xFF4A6D60)   // rótulos, ícones, placeholders — 5,4:1
val Ink400 = Color(0xFF6E9585)   // borda de campo sem foco — 3,1:1
val MintDeep = Color(0xFF1F7A57) // links, cursor, borda com foco — 4,9:1

val White = Color(0xFFFFFFFF)

// Erro (padrão Material 3) — 6,1:1 no fundo
val ErrorRed = Color(0xFFB3261E)
val ErrorContainer = Color(0xFFF9DEDC)
val OnErrorContainer = Color(0xFF410E0B)

// ---------------------------------------------------------------------------
// Medidor de força da senha
// ---------------------------------------------------------------------------
val StrengthWeak = ErrorRed
val StrengthFair = Color(0xFFE0A526)
val StrengthGood = Mint400  // Mint300 quase some sobre o trilho Mint200
val StrengthStrong = MintDeep
