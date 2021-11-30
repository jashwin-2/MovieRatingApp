package com.example.moviereviewapp.utils

interface NetworkMapper <Movie,MovieEntity> {

    fun mapToDbModel(movie : Movie) : MovieEntity
    fun mapFromDbModel(movieEntity : MovieEntity) : Movie
}