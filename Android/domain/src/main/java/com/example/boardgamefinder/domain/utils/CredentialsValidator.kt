package com.example.boardgamefinder.domain.utils

/**
 * Interface for password validation
 */
interface CredentialsValidator {
    // Validate a password
    fun validatePassword(password: String): Boolean
    fun validateEmail(email: String): Boolean
}