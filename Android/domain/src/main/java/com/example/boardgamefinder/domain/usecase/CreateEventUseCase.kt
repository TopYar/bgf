package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.CreatingEvent
import com.example.boardgamefinder.domain.models.Tokens
import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import com.example.boardgamefinder.domain.utils.CredentialsValidator

class CreateEventUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(jwt: String, event: CreatingEvent): Result<Int> {
        return userRepository.createEvent(jwt, event)
    }
}