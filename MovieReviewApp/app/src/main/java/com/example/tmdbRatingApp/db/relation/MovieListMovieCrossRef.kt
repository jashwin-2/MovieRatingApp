package com.example.tmdbRatingApp.db.relation

import androidx.room.Entity

@Entity(primaryKeys = ["listName","id"])
data class MovieListMovieCrossRef(
    val listName : String,
    val id : Int
)
