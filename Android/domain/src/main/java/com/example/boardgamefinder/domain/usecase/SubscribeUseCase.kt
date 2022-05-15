package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.domain.repository.UserRepository

/**
 * Use case for user registration
 *
 * @property userRepository repository to send user credentials
 */
class SubscribeUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(jwt: String, userId: Int): Result<Boolean> {
        return userRepository.subscribe(jwt, userId)
    }
}