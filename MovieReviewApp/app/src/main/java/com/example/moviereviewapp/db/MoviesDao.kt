package com.example.moviereviewapp.db

import androidx.room.*
import com.example.moviereviewapp.db.model.Genre
import com.example.moviereviewapp.db.model.MovieEntity
import com.example.moviereviewapp.db.model.MovieList
import com.example.moviereviewapp.db.relation.MovieListMovieCrossRef
import com.example.moviereviewapp.db.relation.MoviesListWithMovies

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoviesList(lists: List<MovieList>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMoviesListMoviesCrossRefs(relationList: List<MovieListMovieCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genreList: List<Genre>)

    @Query("SELECT * FROM genres")
    fun getGenres(): List<Genre>

    @Query("DELETE FROM movielistmoviecrossref WHERE listName = :WATCH_LIST ")
    suspend fun deleteWatchlist(WATCH_LIST : String = "watchlist_movies")

    @Query("DELETE FROM movielistmoviecrossref WHERE listName = :FAVORITE ")
    suspend fun deleteFavoriteList(FAVORITE : String = "favorite_movies")

    @Transaction
    @Query("SELECT * FROM movies_list WHERE listName = :moviesListName")
    fun getMoviesOfList(moviesListName: String): MoviesListWithMovies

    @Transaction
    suspend fun deleteAndAddWatchList(relationList: List<MovieListMovieCrossRef>){
        deleteWatchlist()
        insertMoviesListMoviesCrossRefs(relationList)
    }

    @Query("SELECT * FROM movies")
    fun getAllMovies() : List<MovieEntity>

    @Transaction
    suspend fun deleteAndAddFavoriteList(relationList: List<MovieListMovieCrossRef>){
        deleteFavoriteList()
        insertMoviesListMoviesCrossRefs(relationList)
    }

    @Query("SELECT * FROM movies WHERE original_title LIKE '%' || :search || '%'")
    fun searchMovies(search : String) : List<MovieEntity>
}