package com.example.moviereviewapp.db.model

import android.util.Log
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.utils.Constants.QUERY_PAGE_SIZE
import com.example.moviereviewapp.utils.NetworkMapper

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

    fun getSavedMovies(list: List<MovieEntity>, page: Int): List<Movie> {
        var newList = list.sortedBy { it.addedTime }
//        var toIndex = 0
//        val totalPages = (newList.size + 1) / QUERY_PAGE_SIZE
//
//        if (totalPages == 0)
//            return emptyList()
//        toIndex = when {
//            page == 1 -> 20
//            totalPages > page -> (page * QUERY_PAGE_SIZE)
//
//            else -> return emptyList()
//        }
        Log.d("Pagination", "${newList.size} mun")


         //   newList.subList(toIndex - 20, toIndex)

        Log.d("Pagination", "${newList.size} pin")

        return newList.map { mapFromDbModel(it) }
    }

}