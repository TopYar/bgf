package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.models.User
import com.example.boardgamefinder.domain.repository.UserRepository

class GetEventMembersUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(jwt: String, eventId: Int): Result<List<User>> {
        return userRepository.getEventVisitors(jwt, eventId)
    }
}