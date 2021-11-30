package com.example.moviereviewapp.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "genres"
)
data class Genre(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)