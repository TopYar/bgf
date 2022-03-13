package com.example.boardgamefinder.domain.models

import com.example.boardgamefinder.domain.exceptions.PasswordValidationException
import com.example.boardgamefinder.domain.utils.PasswordValidator

/**
 * A class for storing and validating user credentials
 */
data class UserCredentials(private val email: Email, private val password: Password) {
    fun validate(passwordValidator: PasswordValidator): Result<Boolean> {
        // validates password
        if (!passwordValidator.validate(password.value))
            return Result.failure(PasswordValidationException())
        // success validation
        return Result.success(true)
    }
}

@JvmInline
value class Email(val value: String)

@JvmInline
value class Password(val value: String)