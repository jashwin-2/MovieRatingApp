package com.example.tmdbRatingApp.model

data class MovieListResponse(
    val page: Int = 0,
    var results: MutableList<Movie>,
    val total_pages: Int = 0,
    val total_results: Int = 0
)