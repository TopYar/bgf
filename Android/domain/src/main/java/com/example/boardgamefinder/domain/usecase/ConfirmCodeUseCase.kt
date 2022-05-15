package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.Tokens
import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import com.example.boardgamefinder.domain.utils.CredentialsValidator

/**
 * Use case for user registration
 *
 * @property userRepository repository to send user credentials
 */
class ConfirmCodeUseCase(
    private val userRepository: UserRepository
) {
    // ToDo create inline classes
    suspend fun execute(code: String, jwt: String): Result<Tokens> {
        return userRepository.confirmCode(code, jwt)
    }
}