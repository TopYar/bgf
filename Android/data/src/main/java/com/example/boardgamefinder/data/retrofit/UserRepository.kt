package com.example.boardgamefinder.data.retrofit

import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.repository.UserRepository
import kotlinx.coroutines.*
import retrofit2.Response

class UserRepository : UserRepository {
    override fun register(credentials: UserCredentials): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getBreeds(): Result<List<String>> {
        var result: Result<List<String>>?

        withContext(Dispatchers.IO) {
            val response: Response<List<String>>
            try {
                response = ApiClient.instance.getBreeds()
            }catch (e: Exception){
                result = Result.failure(DataException.InternetException())
                return@withContext
            }
            if (response.isSuccessful && response.body() != null) {
                result = Result.success(response.body()!!)
                return@withContext
            }

            result = Result.failure(DataException.responseCodeToException(response.code()))
        }

        return result!!
    }

}