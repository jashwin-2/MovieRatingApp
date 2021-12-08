package com.example.tmdbRatingApp.db.model

import android.util.Log
import com.example.tmdbRatingApp.model.Movie
import com.example.tmdbRatingApp.utils.NetworkMapper

class MovieEntityMapper : NetworkMapper<Movie, MovieEntity> {
    override fun mapToDbModel(movie: Movie): MovieEntity {
        return movie.run {
            MovieEntity(
                id,
                adult,
                backdrop_path,
                genre_ids,
                original_language,
                original_title,
                overview,
                popularity,
                poster_path,
                release_date,
                title,
                video,
                vote_average,
                vote_count,
                System.nanoTime()
            )
        }
    }

    override fun mapFromDbModel(movieEntity: MovieEntity): Movie {
        return movieEntity.run {
            Movie(
                id,
                adult,
                backdrop_path,
                genre_ids,
                original_language,
                original_title,
                overview,
                popularity,
                poster_path,
                release_date,
                title,
                video,
                vote_average,
                vote_count
            )
        }
    }

    fun getMovieEntityFromList(list: List<Movie>) = list.map { mapToDbModel(it) }
    fun getMovieFromMovieEntityList(list: List<MovieEntity>) = list.map { mapFromDbModel(it) }

    fun getSavedMovies(list: List<MovieEntity>): List<Movie> {
        var newList = list.sortedBy { it.addedTime }


         //   newList.subList(toIndex - 20, toIndex)

        Log.d("Pagination", "${newList.size} pin")

        return newList.map { mapFromDbModel(it) }
    }

}