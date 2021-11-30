package com.example.moviereviewapp.db

import androidx.room.*
import com.example.moviereviewapp.db.model.Genre
import com.example.moviereviewapp.db.model.MovieEntity
import com.example.moviereviewapp.db.model.MovieList
import com.example.moviereviewapp.db.relation.MovieListMovieCrossRef
import com.example.moviereviewapp.db.relation.MoviesListWithMovies

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoviesList(lists: List<MovieList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoviesListMoviesCrossRefs(relationList: List<MovieListMovieCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genreList: List<Genre>)

    @Query("SELECT * FROM genres")
    fun getGenres(): List<Genre>

    @Transaction
    @Query("SELECT * FROM movies_list WHERE listName = :moviesListName")
    fun getMoviesOfList(moviesListName: String): MoviesListWithMovies

}