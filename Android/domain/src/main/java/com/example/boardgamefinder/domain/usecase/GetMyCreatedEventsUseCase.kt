package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.domain.repository.UserRepository

class GetMyCreatedEventsUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(jwt: String): Result<List<Event>> {
        return userRepository.getMyCreatedEvents(jwt)
    }
}