package com.example.boardgamefinder.domain.usecase

import com.example.boardgamefinder.domain.repository.UserRepository

class GetBreedsUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(): Result<List<String>> {
        return userRepository.getBreeds()
    }
}