package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import com.example.boardgamefinder.domain.utils.CredentialsValidator

/**
 * Use case for user registration
 *
 * @property userRepository repository to send user credentials
 */
class SignUpUseCase(
    private val userRepository: UserRepository,
    private val credentialsValidator: CredentialsValidator
) {
    suspend fun execute(credentials: UserCredentials): Result<String> {
        val credentialsValidationResult = credentials.validate(credentialsValidator)
        if (credentialsValidationResult.isFailure)
            return credentialsValidationResult
        return userRepository.register(credentials)
    }
}