package com.example.tmdbRatingApp.model

data class ErrorResponse(
    val status_code: Int ,
    val status_message: String,
    val success: Boolean
)