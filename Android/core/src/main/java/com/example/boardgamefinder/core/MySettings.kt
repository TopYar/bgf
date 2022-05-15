package com.example.boardgamefinder.core

import android.content.Context
import android.util.TypedValue

object MySettings {
    // Shared prefs
    const val APP_PREFERENCES = "mysettings"
    const val APP_PREFERENCES_TOKEN = "jwt"
    const val APP_PREFERENCES_REFRESH_TOKEN = "refreshJwt"
    const val APP_PREFERENCES_LOGGED_IN = "loggedIn"
    // if user already pass registration and has to input confirmation code
    const val APP_PREFERENCES_CODE_STAGE = "code"

    fun getSecondaryColor(c: Context) : Int{
        val typedValue = TypedValue()
        val theme = c.theme
        theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        return typedValue.data
    }
}