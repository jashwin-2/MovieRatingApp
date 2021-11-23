package com.example.moviereviewapp.model

data class FavoriteBody(
    val favorite: Boolean,
    val media_id: Int,
    val media_type: String
)