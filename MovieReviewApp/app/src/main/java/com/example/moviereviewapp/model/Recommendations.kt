package com.example.moviereviewapp.model

data class Recommendations(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)