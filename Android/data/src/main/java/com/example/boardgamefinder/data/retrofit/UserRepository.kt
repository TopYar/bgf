package com.example.boardgamefinder.data.retrofit

import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import kotlinx.coroutines.*
import retrofit2.Response

/**
 * Implementation for user repository to provide operations with user data
 */
class UserRepository : UserRepository {
    override fun register(credentials: UserCredentials): Result<Boolean> {
        TODO("Not yet implemented")
    }

    // ToDo remove example
    override suspend fun getBreeds(): Result<List<String>> {
        var result: Result<List<String>>?

        withContext(Dispatchers.IO) {
            val response: Response<List<String>>

            try {
                response = ApiClient.instance.getBreeds()
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else
                Result.failure(DataException.responseCodeToException(response.code()))
        }

        return result!!
    }

}