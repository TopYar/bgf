package com.example.boardgamefinder.domain.repository

import com.example.boardgamefinder.domain.models.UserCredentials

/**
 * Interface for interacting with user data
 */
interface UserRepository {
    // register user
    fun register(credentials: UserCredentials): Result<Boolean>
    // ToDo remove example
    suspend fun getBreeds(): Result<List<String>>
}