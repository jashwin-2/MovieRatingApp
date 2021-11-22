package com.example.moviereviewapp.model

data class MovieListResponse(
    val page: Int,
    var results: MutableList<Movie>,
    val total_pages: Int,
    val total_results: Int
)