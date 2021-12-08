package com.example.tmdbRatingApp.model

data class AuthToken(
    val expires_at: String,
    val request_token: String,
    val success: Boolean
)