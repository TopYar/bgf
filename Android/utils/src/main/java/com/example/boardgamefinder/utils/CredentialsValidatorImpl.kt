package com.example.boardgamefinder.utils

import com.example.boardgamefinder.domain.utils.CredentialsValidator
import java.util.regex.Pattern

/**
 * implementation for primary password validator
 */
object CredentialsValidatorImpl : CredentialsValidator{
    // Patterns
    private const val EMAIL_PATTERN = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+[.][a-zA-Z0-9-.]+$"
    private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"

    override fun validatePassword(password: String): Boolean {
        return Pattern.matches(PASSWORD_PATTERN, password)
    }

    override fun validateEmail(email: String): Boolean {
        return Pattern.matches(EMAIL_PATTERN, email)
    }
}