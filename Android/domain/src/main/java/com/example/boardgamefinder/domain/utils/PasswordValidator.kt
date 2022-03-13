package com.example.boardgamefinder.domain.utils

/**
 * Interface for password validation
 */
interface PasswordValidator {
    /**
     * Validate a password
     */
    fun validate(password: String): Boolean
}