package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.Email
import com.example.boardgamefinder.domain.models.Password
import com.example.boardgamefinder.domain.models.Tokens
import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import com.example.boardgamefinder.domain.utils.CredentialsValidator

/**
 * Use case for user registration
 *
 * @property userRepository repository to send user credentials
 */
class LogOutUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(jwt: String): Result<Boolean> {
        return userRepository.logOut(jwt)
    }
}