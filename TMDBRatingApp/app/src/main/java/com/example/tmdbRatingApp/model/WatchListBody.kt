package com.example.tmdbRatingApp.model

data class WatchListBody(
    val media_id: Int,
    val media_type: String,
    val watchlist: Boolean
)