package com.example.boardgamefinder.data.retrofit

/**
 * Class for custom data exceptions
 */
sealed class DataException(message: String) : Exception(message){
    class InternetException : DataException("Something went wrong. Please, check the Internet connection.")

    class Response400 : DataException("Bad request.")
    class Response401 : DataException("Unauthorized.")
    class Response404 : DataException("Not found.")
    class Response422 : DataException("Unprocessable Entity.")
    class Response500 : DataException("Server broken.")
    class UnknownServerError(code: Int) : DataException(code.toString())

    internal companion object {
        // converts server exception code into custom data exception
        fun responseCodeToException(code: Int) = when (code) {
            400 -> Response400()
            401 -> Response401()
            404 -> Response404()
            422 -> Response422()
            500 -> Response500()
            else -> UnknownServerError(code)
        }
    }
}

