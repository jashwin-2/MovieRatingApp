package com.example.tmdbRatingApp.utils

interface NetworkMapper <Movie,MovieEntity> {

    fun mapToDbModel(movie : Movie) : MovieEntity
    fun mapFromDbModel(movieEntity : MovieEntity) : Movie
}