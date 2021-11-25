package com.example.moviereviewapp.repository

import android.app.Application
import android.util.Log
import com.example.moviereviewapp.api.ServiceProvider
import com.example.moviereviewapp.api.service.MovieService
import com.example.moviereviewapp.db.MoviesAppDataBase
import com.example.moviereviewapp.db.MoviesDao
import com.example.moviereviewapp.db.relation.MovieListMovieCrossRef
import com.example.moviereviewapp.model.*
import com.example.moviereviewapp.utils.Constants
import com.example.moviereviewapp.utils.Constants.POPULAR_MOVIES
import com.example.moviereviewapp.utils.Constants.TOP_RATED_MOVIES
import com.example.moviereviewapp.utils.Resource

class MovieRepository(application: Application) : Repository() {
    val movieService: MovieService = ServiceProvider.movieService
    val moviesDao: MoviesDao = MoviesAppDataBase.getInstance(application).movieDao

    init {

    }

    suspend fun getMoviesList(type: String, page: Int = 1): Resource<MovieListResponse> {
        val response = safeApiCall { movieService.getMoviesList(type, Constants.KEY, page) }
        if (response is Resource.Success)
            loadMoviesToDb(response.data?.results ?: listOf(), type)
        return response
    }

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
    ) = movieService.rateTheMovie(movieId, Constants.KEY, sessionId, rating)

    suspend fun getAllGenres() =
        safeApiCall {
            movieService.getAllGenres(Constants.KEY)
        }


    private suspend fun loadMoviesToDb(data: List<Movie>, type: String) {
        moviesDao.insertMovies(data)
        val list = mutableListOf<MovieListMovieCrossRef>()
        data.forEach { list.add(MovieListMovieCrossRef(type, it.id)) }
        moviesDao.inertMoviesListMoviesCrossRefs(list)
    }

}