package com.apccarvalho.authapp.domain

/** Critérios exibidos como checklist no cadastro */
enum class PasswordCriterion {
    MIN_LENGTH,   // 8 ou mais caracteres
    LOWERCASE,
    UPPERCASE,
    DIGIT,
    SPECIAL,
}

enum class StrengthLevel { EMPTY, WEAK, FAIR, GOOD, STRONG }

data class PasswordStrength(
    val level: StrengthLevel,
    val met: Set<PasswordCriterion>,
) {
    /** Preenchimento da barra: 0f a 1f. */
    val progress: Float
        get() = when (level) {
            StrengthLevel.EMPTY -> 0f
            StrengthLevel.WEAK -> 0.25f
            StrengthLevel.FAIR -> 0.5f
            StrengthLevel.GOOD -> 0.75f
            StrengthLevel.STRONG -> 1f
        }

    companion object {
        const val RECOMMENDED_LENGTH = 8

        fun evaluate(password: String): PasswordStrength {
            if (password.isEmpty()) return PasswordStrength(StrengthLevel.EMPTY, emptySet())

            val met = buildSet {
                if (password.length >= RECOMMENDED_LENGTH) add(PasswordCriterion.MIN_LENGTH)
                if (password.any { it.isLowerCase() }) add(PasswordCriterion.LOWERCASE)
                if (password.any { it.isUpperCase() }) add(PasswordCriterion.UPPERCASE)
                if (password.any { it.isDigit() }) add(PasswordCriterion.DIGIT)
                if (password.any { !it.isLetterOrDigit() && !it.isWhitespace() }) {
                    add(PasswordCriterion.SPECIAL)
                }
            }

            val level = when {
                // Abaixo do mínimo do Firebase é sempre fraca, não importa a variedade.
                password.length < Validators.FIREBASE_MIN_PASSWORD -> StrengthLevel.WEAK
                met.size <= 2 -> StrengthLevel.WEAK
                met.size == 3 -> StrengthLevel.FAIR
                met.size == 4 -> StrengthLevel.GOOD
                else -> StrengthLevel.STRONG
            }
            return PasswordStrength(level, met)
        }
    }
}
