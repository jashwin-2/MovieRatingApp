package com.example.moviereviewapp.model

data class AuthToken(
    val expires_at: String,
    val request_token: String,
    val success: Boolean
)