package com.example.tmdbRatingApp.model

import com.example.tmdbRatingApp.db.model.Genre

data class GenreListResponse(
    val genres: List<Genre>
)