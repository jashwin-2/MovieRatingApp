package com.example.moviereviewapp.db

import androidx.room.*
import com.example.moviereviewapp.db.relation.MovieListMovieCrossRef
import com.example.moviereviewapp.db.relation.MoviesListWithMovies
import com.example.moviereviewapp.model.Movie
@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies : List<Movie>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoviesList(lists : List<MovieList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inertMoviesListMoviesCrossRefs(relationList : List<MovieListMovieCrossRef>)

   @Transaction
   @Query("SELECT * FROM movies_list WHERE listName = :moviesListName")
   suspend fun getMoviesOfList(moviesListName : String) : List<MoviesListWithMovies>

}