package com.example.moviereviewapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies_list")
data class MovieList(
    @PrimaryKey(autoGenerate = false)
    val listName : String
)
