package com.example.tmdbRatingApp.model

data class LoginRequest(
    val username: String,
    val password: String,
    val request_token: String

)