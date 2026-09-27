package com.apccarvalho.authapp.ui.common

import androidx.annotation.StringRes
import com.apccarvalho.authapp.R
import com.apccarvalho.authapp.domain.AuthError
import com.apccarvalho.authapp.domain.FieldError
import com.apccarvalho.authapp.domain.PasswordCriterion
import com.apccarvalho.authapp.domain.StrengthLevel

/*
 * Tradução dos tipos do domínio para textos de strings.xml.
 * O domínio fica livre de Android; só a camada de UI conhece R.string.
 */

@StringRes
fun FieldError.messageRes(): Int = when (this) {
    FieldError.REQUIRED -> R.string.error_required
    FieldError.INVALID_EMAIL -> R.string.error_invalid_email
    FieldError.PASSWORD_TOO_SHORT -> R.string.error_password_too_short
    FieldError.PASSWORDS_DONT_MATCH -> R.string.error_passwords_dont_match
    FieldError.NAME_TOO_SHORT -> R.string.error_name_too_short
    FieldError.NAME_TOO_LONG -> R.string.error_name_too_long
}

@StringRes
fun AuthError.messageRes(): Int = when (this) {
    AuthError.EMAIL_ALREADY_IN_USE -> R.string.auth_error_email_in_use
    AuthError.WEAK_PASSWORD -> R.string.auth_error_weak_password
    AuthError.INVALID_EMAIL -> R.string.auth_error_invalid_email
    AuthError.INVALID_CREDENTIALS -> R.string.auth_error_invalid_credentials
    AuthError.USER_DISABLED -> R.string.auth_error_user_disabled
    AuthError.TOO_MANY_REQUESTS -> R.string.auth_error_too_many_requests
    AuthError.NETWORK -> R.string.auth_error_network
    AuthError.OPERATION_NOT_ALLOWED -> R.string.auth_error_operation_not_allowed
    AuthError.UNKNOWN -> R.string.auth_error_unknown
}

@StringRes
fun StrengthLevel.labelRes(): Int = when (this) {
    StrengthLevel.EMPTY -> R.string.strength_empty
    StrengthLevel.WEAK -> R.string.strength_weak
    StrengthLevel.FAIR -> R.string.strength_fair
    StrengthLevel.GOOD -> R.string.strength_good
    StrengthLevel.STRONG -> R.string.strength_strong
}

@StringRes
fun PasswordCriterion.labelRes(): Int = when (this) {
    PasswordCriterion.MIN_LENGTH -> R.string.criterion_min_length
    PasswordCriterion.LOWERCASE -> R.string.criterion_lowercase
    PasswordCriterion.UPPERCASE -> R.string.criterion_uppercase
    PasswordCriterion.DIGIT -> R.string.criterion_digit
    PasswordCriterion.SPECIAL -> R.string.criterion_special
}
