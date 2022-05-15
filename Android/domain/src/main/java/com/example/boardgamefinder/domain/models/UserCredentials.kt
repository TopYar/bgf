package com.example.boardgamefinder.domain.models

import com.example.boardgamefinder.domain.exceptions.EmailValidationException
import com.example.boardgamefinder.domain.exceptions.PasswordMatchException
import com.example.boardgamefinder.domain.exceptions.PasswordValidationException
import com.example.boardgamefinder.domain.utils.CredentialsValidator

/**
 * A class for storing and validating user credentials
 */
data class UserCredentials(val email: Email, val password: Password, val passwordConf: Password) {
    //ToDo move to usecase
    fun validate(credentialsValidator: CredentialsValidator): Result<String> {
        if(password != passwordConf)
            return Result.failure(PasswordMatchException())
        // validates email
        if (!credentialsValidator.validateEmail(email.value))
            return Result.failure(EmailValidationException())
        // validates password
        if (!credentialsValidator.validatePassword(password.value))
            return Result.failure(PasswordValidationException())
        // success validation
        return Result.success("")
    }
}

@JvmInline
value class Email(val value: String)

@JvmInline
value class Password(val value: String)