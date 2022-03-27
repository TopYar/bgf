package com.example.boardgamefinder.utils

import com.example.boardgamefinder.domain.utils.PasswordValidator

/**
 * implementation for primary password validator
 */
object PasswordValidatorImpl : PasswordValidator{
    override fun validate(password: String): Boolean {
        //TODO
        return password.length >= 8
    }
}