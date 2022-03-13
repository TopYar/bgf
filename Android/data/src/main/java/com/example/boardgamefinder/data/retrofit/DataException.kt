package com.example.boardgamefinder.data.retrofit

sealed class DataException(message: String) : Exception(){
    class InternetException : DataException("Something went wrong. Please, check the Internet connection.")
    class Response500 : DataException("Server broken")
    class UnknownServerError : DataException("Unknown error")

    internal companion object {
        fun responseCodeToException(code: Int) = when (code) {
            500 -> Response500()
            else -> UnknownServerError()
        }
    }
}
