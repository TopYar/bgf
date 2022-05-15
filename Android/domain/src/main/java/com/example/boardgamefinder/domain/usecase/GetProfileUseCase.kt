package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.Tokens
import com.example.boardgamefinder.domain.models.User
import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import com.example.boardgamefinder.domain.utils.CredentialsValidator

/**
 * Use case for user registration
 *
 * @property userRepository repository to send user credentials
 */
class GetProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(jwt: String, userId: Int = -1): Result<User> {
        return if(userId == -1)
            userRepository.getProfile(jwt)
        else
            userRepository.getProfile(jwt, userId)
    }
}