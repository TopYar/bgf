package com.example.boardgamefinder.data.retrofit

import com.example.boardgamefinder.data.retrofit.models.CodeRequest
import com.example.boardgamefinder.data.retrofit.models.EmailRequest
import com.example.boardgamefinder.data.retrofit.models.GenericResponse
import com.example.boardgamefinder.data.retrofit.models.UserCredentialsRequest
import com.example.boardgamefinder.domain.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API for app network operations
 */
internal interface BoardGameFinderApi {
    @GET("events")
    suspend fun getEvents(
        @Header ("jwt") jwt: String,
    ): Response<GenericResponse<List<Event>>>

    @POST("profile")
    suspend fun register(
        @Body userCredentials: UserCredentialsRequest
    ): Response<GenericResponse<Token>>

    @PUT("profile/confirm")
    suspend fun confirmCode(
        @Header ("jwt") jwt: String,
        @Body code: CodeRequest
    ): Response<GenericResponse<Tokens>>

    @POST("auth/sign-in")
    suspend fun logIn(
        @Body userCredentials: UserCredentialsRequest
    ): Response<GenericResponse<Tokens>>

    @DELETE("auth/sign-out")
    suspend fun logOut(
        @Header ("jwt") jwt: String
    ): Response<GenericResponse<Boolean>>

    @POST("events")
    suspend fun createEvent(
        @Header ("jwt") jwt: String,
        @Body creatingEvent: CreatingEvent
    ): Response<GenericResponse<Int>>

    @GET("events/visitors")
    suspend fun getEventVisitors(
        @Header ("jwt") jwt: String,
        @Query("eventId") id: Int
    ): Response<GenericResponse<List<User>>>

    @POST("events/likes")
    suspend fun setLike(
        @Header ("jwt") jwt: String,
        @Query("eventId") id: Int
    ): Response<GenericResponse<Unit>>

    @DELETE("events/likes")
    suspend fun removeLike(
        @Header ("jwt") jwt: String,
        @Query("eventId") id: Int
    ): Response<GenericResponse<Unit>>

    @POST("events/participate")
    suspend fun joinEvent(
        @Header ("jwt") jwt: String,
        @Query("eventId") id: Int
    ): Response<GenericResponse<Event.SubscriptionStatus>>

    @DELETE("events/participate")
    suspend fun leaveEvent(
        @Header ("jwt") jwt: String,
        @Query("eventId") id: Int
    ): Response<GenericResponse<Unit>>

    @GET("events")
    suspend fun getEvent(
        @Header ("jwt") jwt: String,
        @Query("eventId") id: Int
    ): Response<GenericResponse<Event>>

    @POST("auth/recover")
    suspend fun recoverPassword(
        @Body email: EmailRequest
    ): Response<GenericResponse<Boolean>>

    @GET("profile")
    suspend fun getProfile(
        @Header ("jwt") jwt: String
    ): Response<GenericResponse<User>>

    @GET("profile")
    suspend fun getProfile(
        @Header ("jwt") jwt: String,
        @Query("id") id: Int
    ): Response<GenericResponse<User>>

    @POST("subscriptions")
    suspend fun subscribe(
        @Header ("jwt") jwt: String,
        @Query("id") id: Int
    ): Response<GenericResponse<Boolean>>

    @DELETE("subscriptions")
    suspend fun unsubscribe(
        @Header ("jwt") jwt: String,
        @Query("id") id: Int
    ): Response<GenericResponse<Boolean>>

    @GET("events/my/created")
    suspend fun getMyCreatedEvents(
        @Header ("jwt") jwt: String
    ): Response<GenericResponse<List<Event>>>
}