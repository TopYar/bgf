package com.example.boardgamefinder.data.retrofit

import com.example.boardgamefinder.data.retrofit.models.CodeRequest
import com.example.boardgamefinder.data.retrofit.models.EmailRequest
import com.example.boardgamefinder.data.retrofit.models.GenericResponse
import com.example.boardgamefinder.data.retrofit.models.UserCredentialsRequest
import com.example.boardgamefinder.domain.models.*
import com.example.boardgamefinder.domain.repository.UserRepository
import kotlinx.coroutines.*
import retrofit2.Response

/**
 * Implementation for user repository to provide operations with user data
 */
class UserRepository : UserRepository {
    // ToDo ask for error codes
    override suspend fun register(credentials: UserCredentials): Result<String> {
        var result: Result<String>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Token>>

            try {
                response = ApiClient.instance.register(
                    UserCredentialsRequest(credentials.email, credentials.password)
                )
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result.token)
        }

        return result!!
    }

    override suspend fun logIn(email: Email, password: Password): Result<Tokens> {
        var result: Result<Tokens>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Tokens>>

            try {
                response = ApiClient.instance.logIn(
                    UserCredentialsRequest(email, password)
                )
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun logOut(jwt: String): Result<Boolean> {
        var result: Result<Boolean>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Boolean>>

            try {
                response = ApiClient.instance.logOut(jwt)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun confirmCode(code: String, jwt: String): Result<Tokens>{
        var result: Result<Tokens>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Tokens>>

            try {
                response = ApiClient.instance.confirmCode(jwt, CodeRequest(code))
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun getEvents(jwt: String): Result<List<Event>> {
        var result: Result<List<Event>>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<List<Event>>>

            try {
                response = ApiClient.instance.getEvents(jwt)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                //Result.failure(DataException.responseCodeToException(response.body()?.error?.code ?: response.code()))
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun createEvent(jwt: String, event: CreatingEvent): Result<Int> {
        var result: Result<Int>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Int>>

            try {
                response = ApiClient.instance.createEvent(jwt, event)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun getEventVisitors(jwt: String, eventId: Int): Result<List<User>> {
        var result: Result<List<User>>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<List<User>>>

            try {
                response = ApiClient.instance.getEventVisitors(jwt, eventId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun setLike(jwt: String, eventId: Int): Result<Unit> {
        var result: Result<Unit>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Unit>>

            try {
                response = ApiClient.instance.setLike(jwt, eventId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun removeLike(jwt: String, eventId: Int): Result<Unit> {
        var result: Result<Unit>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Unit>>

            try {
                response = ApiClient.instance.removeLike(jwt, eventId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun joinEvent(jwt: String, eventId: Int): Result<Event.SubscriptionStatus> {
        var result: Result<Event.SubscriptionStatus>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Event.SubscriptionStatus>>

            try {
                response = ApiClient.instance.joinEvent(jwt, eventId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun leaveEvent(jwt: String, eventId: Int): Result<Unit> {
        var result: Result<Unit>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Unit>>

            try {
                response = ApiClient.instance.leaveEvent(jwt, eventId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun getEvent(jwt: String, eventId: Int): Result<Event> {
        var result: Result<Event>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Event>>

            try {
                response = ApiClient.instance.getEvent(jwt, eventId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun recoverPassword(email: String): Result<Boolean> {
        var result: Result<Boolean>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Boolean>>

            try {
                response = ApiClient.instance.recoverPassword(EmailRequest(email))
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun getProfile(jwt: String) : Result<User> {
        var result: Result<User>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<User>>

            try {
                response = ApiClient.instance.getProfile(jwt)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun getProfile(jwt: String, userId: Int) : Result<User> {
        var result: Result<User>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<User>>

            try {
                response = ApiClient.instance.getProfile(jwt, userId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun subscribe(jwt: String, userId: Int) : Result<Boolean> {
        var result: Result<Boolean>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Boolean>>

            try {
                response = ApiClient.instance.subscribe(jwt, userId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun unsubscribe(jwt: String, userId: Int) : Result<Boolean> {
        var result: Result<Boolean>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<Boolean>>

            try {
                response = ApiClient.instance.unsubscribe(jwt, userId)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }

    override suspend fun getMyCreatedEvents(jwt: String): Result<List<Event>> {
        var result: Result<List<Event>>?

        withContext(Dispatchers.IO) {
            val response: Response<GenericResponse<List<Event>>>

            try {
                response = ApiClient.instance.getMyCreatedEvents(jwt)
            } catch (e: Exception) {
                result = Result.failure(DataException.InternetException())
                return@withContext
            }

            result = if(!response.isSuccessful || !response.body()!!.success)
                Result.failure(DataException.responseCodeToException(response.code()))
            else
                Result.success(response.body()!!.result)
        }

        return result!!
    }
}