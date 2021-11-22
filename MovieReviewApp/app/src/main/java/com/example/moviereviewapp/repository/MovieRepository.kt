package com.example.moviereviewapp.repository

import com.example.moviereviewapp.api.ServiceProvider
import com.example.moviereviewapp.api.service.MovieService
import com.example.moviereviewapp.utils.Constants

class MovieRepository : Repository() {
    private val movieService: MovieService = ServiceProvider.movieService
    suspend fun getMoviesList(type: String, page: Int = 1) =
        safeApiCall { movieService.getMoviesList(type, Constants.KEY, page) }

    suspend fun getMovieDetails(id: Int, sessionId: String) =
        safeApiCall { movieService.getMovieDetail( id,Constants.KEY,sessionId) }

    suspend fun searchMovie(searchQuery: String, searchMoviesPageCount: Int = 1) =
        safeApiCall { movieService.searchMovie(searchQuery,Constants.KEY,searchMoviesPageCount)
        }

    suspend fun getFavoriteMovies(id:Int , sessionId: String,page: Int = 1, )=
        safeApiCall { movieService.getFavorite(id,Constants.KEY,sessionId) }

    suspend fun getWatchListMovies(id:Int , sessionId: String,page: Int = 1, )=
        safeApiCall { movieService.getWatchList(id,Constants.KEY,sessionId) }

    suspend fun getMoviesByGenre(id: Int, genrePageCount: Int)=
        safeApiCall { movieService.getMoviesByGenre(id,Constants.KEY,genrePageCount) }
}