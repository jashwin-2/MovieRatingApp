package com.example.moviereviewapp.repository

import com.example.moviereviewapp.api.ServiceProvider
import com.example.moviereviewapp.api.service.MovieService
import com.example.moviereviewapp.model.FavoriteBody
import com.example.moviereviewapp.model.Rating
import com.example.moviereviewapp.model.WatchListBody
import com.example.moviereviewapp.utils.Constants

class MovieRepository : Repository() {
    val movieService: MovieService = ServiceProvider.movieService
    suspend fun getMoviesList(type: String, page: Int = 1) =
        safeApiCall { movieService.getMoviesList(type, Constants.KEY, page) }

    suspend fun getMovieDetails(id: Int, sessionId: String) =
        safeApiCall { movieService.getMovieDetail(id, Constants.KEY, sessionId) }

    suspend fun searchMovie(searchQuery: String, searchMoviesPageCount: Int = 1) =
        safeApiCall {
            movieService.searchMovie(searchQuery, Constants.KEY, searchMoviesPageCount)
        }

    suspend fun getFavoriteMovies(id: Int, sessionId: String, page: Int = 1) =
        safeApiCall { movieService.getFavorite(id, Constants.KEY, sessionId) }

    suspend fun getWatchListMovies(id: Int, sessionId: String, page: Int = 1) =
        safeApiCall { movieService.getWatchList(id, Constants.KEY, sessionId) }

    suspend fun getMoviesByGenre(id: Int, genrePageCount: Int) =
        safeApiCall { movieService.getMoviesByGenre(id, Constants.KEY, genrePageCount) }

    suspend fun addOrRemoveMovieToFavorite(body: FavoriteBody, sessionId: String, accountId: Int) =

        movieService.addOrRemoveMovieToFavorite(
            accountId,
            body,
            Constants.KEY,
            sessionId
        )


    suspend fun addOrRemoveMovieToWatchList(
        body: WatchListBody,
        sessionId: String,
        accountId: Int
    ) =
        movieService.addOrRemoveMovieToWatchList(
            accountId,
            body,
            Constants.KEY,
            sessionId
        )

    suspend fun rateTheMovie(
        movieId: Int,
        sessionId: String,
        rating: Rating
    ) = movieService.rateTheMovie(movieId, Constants.KEY, sessionId,rating)

    suspend fun getAllGenres() =
        safeApiCall {
            movieService.getAllGenres(Constants.KEY)
        }
}