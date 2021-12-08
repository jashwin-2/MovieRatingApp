package com.example.tmdbRatingApp.model

data class FavoriteBody(
    val favorite: Boolean,
    val media_id: Int,
    val media_type: String
)