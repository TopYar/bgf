package com.example.boardgamefinder.core

import android.content.Context
import com.example.boardgamefinder.data.retrofit.DataException
import com.example.boardgamefinder.domain.exceptions.EmailValidationException
import com.example.boardgamefinder.domain.exceptions.PasswordMatchException
import com.example.boardgamefinder.domain.exceptions.PasswordValidationException
import java.lang.Exception

/**
 * Converts custom data exception into a message for user
 */
fun Exception.toMessage(c: Context): String {
    return when(this){
        is DataException.InternetException -> c.getString(R.string.InternetException)
        is DataException.Response422 -> c.getString(R.string.Response422)
        is DataException.Response400 -> c.getString(R.string.Response500)
        is DataException.Response401 -> c.getString(R.string.Response500)
        is DataException.Response404 -> c.getString(R.string.Response500)
        is DataException.Response500 -> c.getString(R.string.Response500)
        //is DataException.UnknownServerError -> c.getString(R.string.UnknownServerError)
        is DataException.UnknownServerError -> this.message.toString()

        is PasswordValidationException -> c.getString(R.string.IncorrectPassword)
        is EmailValidationException -> c.getString(R.string.IncorrectEmail)
        is PasswordMatchException -> c.getString(R.string.PasswordDoNotMatch)

        else -> c.getString(R.string.AnotherError)
    }
}