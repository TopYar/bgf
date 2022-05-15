package com.example.boardgamefinder.data.retrofit.models

data class GenericResponse<T>(val success: Boolean, val result: T, val error: ErrorFromServer)