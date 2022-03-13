package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import com.example.boardgamefinder.domain.utils.PasswordValidator

/**
 * Use case for user registration
 *
 * @property userRepository repository to send user credentials
 */
class RegisterUseCase(
    private val userRepository: UserRepository,
    private val passwordValidator: PasswordValidator
) {

    fun execute(credentials: UserCredentials): Result<Boolean> {
        val credentialsValidationResult = credentials.validate(passwordValidator)
        if (credentialsValidationResult.isFailure)
            return credentialsValidationResult
        return userRepository.register(credentials)
    }
}