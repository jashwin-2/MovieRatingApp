package com.example.tmdbRatingApp.model

data class Review(
    val author: String,
    val author_details: AuthorDetails,
    val content: String,
    val created_at: String,
    val id: String,
    val updated_at: String,
    val url: String,
    var isExpanded:Boolean = false
)