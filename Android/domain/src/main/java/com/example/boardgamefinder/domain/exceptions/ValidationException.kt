package com.example.boardgamefinder.domain.exceptions

import java.lang.Exception

/**
 * Exception on failed password validation
 */
class PasswordValidationException() : Exception("Incorrect password")