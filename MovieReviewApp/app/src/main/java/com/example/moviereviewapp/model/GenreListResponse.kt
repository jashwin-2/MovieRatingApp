package com.example.moviereviewapp.model

import com.example.moviereviewapp.db.model.Genre

data class GenreListResponse(
    val genres: List<Genre>
)