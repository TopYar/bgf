package com.example.boardgamefinder.domain.exceptions

import java.lang.Exception

/**
 * Exception on failed password validation
 */
class PasswordValidationException() : Exception("Incorrect password")

class PasswordMatchException() : Exception("Passwords do not match")

class EmailValidationException() : Exception("Incorrect email")