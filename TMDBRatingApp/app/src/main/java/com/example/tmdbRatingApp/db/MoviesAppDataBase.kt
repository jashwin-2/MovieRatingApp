package com.example.tmdbRatingApp.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tmdbRatingApp.db.model.Genre
import com.example.tmdbRatingApp.db.model.MovieEntity
import com.example.tmdbRatingApp.db.model.MovieList
import com.example.tmdbRatingApp.db.relation.MovieListMovieCrossRef
import com.example.tmdbRatingApp.utils.Constants.FAVORITE_MOVIES
import com.example.tmdbRatingApp.utils.Constants.NOW_PLAYING_MOVIES
import com.example.tmdbRatingApp.utils.Constants.POPULAR_MOVIES
import com.example.tmdbRatingApp.utils.Constants.TOP_RATED_MOVIES
import com.example.tmdbRatingApp.utils.Constants.UPCOMING_MOVIES
import com.example.tmdbRatingApp.utils.Constants.WATCHLIST_MOVIES
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        MovieEntity::class,
        MovieList::class,
        MovieListMovieCrossRef::class,
        Genre::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MoviesAppDataBase : RoomDatabase() {
    abstract val movieDao: MoviesDao

    companion object {
        @Volatile
        var INSTANCE: MoviesAppDataBase? = null

        fun getInstance(context: Context): MoviesAppDataBase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MoviesAppDataBase::class.java,
                    "movies_db"
                ).build().also {
                    addMoviesListRows(it)
                    INSTANCE = it
                }
            }
        }

        private fun addMoviesListRows(it: MoviesAppDataBase) {
            GlobalScope.launch {
                Log.d("DataBase", "Callles")
                it.movieDao.insertMoviesList(
                    listOf(
                        MovieList(POPULAR_MOVIES),
                        MovieList(TOP_RATED_MOVIES),
                        MovieList(UPCOMING_MOVIES),
                        MovieList(NOW_PLAYING_MOVIES),
                        MovieList(WATCHLIST_MOVIES),
                        MovieList(FAVORITE_MOVIES),
                    )
                )
            }
        }
    }


}