package com.example.moviereviewapp.db.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.moviereviewapp.db.model.MovieEntity
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.db.model.MovieList

data class MoviesListWithMovies(
    @Embedded
    val moviesList : MovieList,
    @Relation(
        parentColumn = "listName",
        entityColumn = "id",
        associateBy = Junction(MovieListMovieCrossRef :: class)
    )
    val movies : List<MovieEntity>
)
