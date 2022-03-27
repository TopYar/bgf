package com.example.boardgamefinder.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Client for network
 */
internal object ApiClient {
    // ToDo change url
    private const val BASE_URL = "https://na-povodke.ru/"

    val instance: BoardGameFinderApi by lazy{
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        retrofit.create(BoardGameFinderApi::class.java)
    }
}