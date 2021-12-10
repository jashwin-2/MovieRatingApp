package com.example.tmdbRatingApp.model

data class ReviewResponse(
    val id: Int = 0,
    val page: Int = 0,
    val results: MutableList<Review> = mutableListOf(),
    val total_pages: Int = 0,
    val total_results: Int = 0
)
