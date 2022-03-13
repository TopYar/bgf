package com.example.boardgamefinder.core

import com.example.boardgamefinder.data.retrofit.DataException
import java.lang.Exception

object ServerExceptionConverter {
    fun convertServerExceptionToText(exception: Exception): String{
        return when(exception){
            is DataException.InternetException -> "Something went wrong. Please, check the Internet connection"
            is DataException.Response500 -> "Server is broken"
            is DataException.UnknownServerError -> "Something went wrong"
            else -> "Error"
        }
    }
}