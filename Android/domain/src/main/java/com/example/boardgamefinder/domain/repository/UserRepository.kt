package com.example.boardgamefinder.domain.repository

import com.example.boardgamefinder.domain.models.UserCredentials

/**
 * Interface for interacting with user data
 */
interface UserRepository {
    /**
     * User registration
     */
    fun register(credentials: UserCredentials): Result<Boolean>
    suspend fun getBreeds(): Result<List<String>>
}