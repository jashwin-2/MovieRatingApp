package com.example.tmdbRatingApp.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies_list")
data class MovieList(
    @PrimaryKey(autoGenerate = false)
    val listName : String
)
