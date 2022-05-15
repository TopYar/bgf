package com.example.boardgamefinder.data.retrofit.models

import com.example.boardgamefinder.domain.models.Email
import com.example.boardgamefinder.domain.models.Password

data class UserCredentialsRequest (val email: Email, val password: Password)