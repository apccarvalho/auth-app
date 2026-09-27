package com.apccarvalho.authapp.domain

/** Motivo pelo qual um campo é inválido. A UI converte em texto (ui/common/Messages.kt). */
enum class FieldError {
    REQUIRED,
    INVALID_EMAIL,
    PASSWORD_TOO_SHORT,
    PASSWORDS_DONT_MATCH,
    NAME_TOO_SHORT,
    NAME_TOO_LONG,
}

/**
 * Validações puras
 */
object Validators {

    /** Mínimo exigido pelo Firebase Authentication. */
    const val FIREBASE_MIN_PASSWORD = 6
    const val NAME_MIN = 2
    const val NAME_MAX = 40

    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    fun email(value: String): FieldError? {
        val email = value.trim()
        return when {
            email.isEmpty() -> FieldError.REQUIRED
            !EMAIL_REGEX.matches(email) -> FieldError.INVALID_EMAIL
            else -> null
        }
    }

    /** No login só checamos se foi preenchida — regras de força valem no cadastro. */
    fun loginPassword(value: String): FieldError? =
        if (value.isEmpty()) FieldError.REQUIRED else null

    fun newPassword(value: String): FieldError? = when {
        value.isEmpty() -> FieldError.REQUIRED
        value.length < FIREBASE_MIN_PASSWORD -> FieldError.PASSWORD_TOO_SHORT
        else -> null
    }

    fun confirmPassword(password: String, confirmation: String): FieldError? = when {
        confirmation.isEmpty() -> FieldError.REQUIRED
        confirmation != password -> FieldError.PASSWORDS_DONT_MATCH
        else -> null
    }

    fun name(value: String): FieldError? {
        val name = value.trim()
        return when {
            name.isEmpty() -> FieldError.REQUIRED
            name.length < NAME_MIN -> FieldError.NAME_TOO_SHORT
            name.length > NAME_MAX -> FieldError.NAME_TOO_LONG
            else -> null
        }
    }
}
