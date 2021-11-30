package com.example.moviereviewapp.repository

import android.app.Application
import android.util.Log
import com.example.moviereviewapp.api.ServiceProvider
import com.example.moviereviewapp.api.service.MovieService
import com.example.moviereviewapp.db.MoviesAppDataBase
import com.example.moviereviewapp.db.MoviesDao
import com.example.moviereviewapp.db.model.MovieEntityMapper
import com.example.moviereviewapp.db.relation.MovieListMovieCrossRef
import com.example.moviereviewapp.model.*
import com.example.moviereviewapp.utils.Constants
import com.example.moviereviewapp.utils.Constants.FAVORITE_MOVIES
import com.example.moviereviewapp.utils.Constants.WATCHLIST_MOVIES
import com.example.moviereviewapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import networkBoundResource

class MovieRepository(application: Application) : Repository() {
    val mapper = MovieEntityMapper()
    val movieService: MovieService = ServiceProvider.movieService
    val moviesDao: MoviesDao

    init {
        moviesDao = MoviesAppDataBase.getInstance(application).movieDao
    }

    suspend fun getMoviesList(type: String, page: Int): Resource<MovieListResponse> {

        return networkBoundResource<MovieListResponse, Resource<MovieListResponse>>(
            queryLocalData = {
                fetchMoviesOfList(type, page)
            },
            fetch = { safeApiCall { movieService.getMoviesList(type, Constants.KEY, page) } },
            saveFetchResult = { response, type ->
                val movies = response.data?.results ?: listOf()
                loadMoviesToDb(movies, type)
            },
            typeOfList = type,
            queryCurrent = {
                it.data!!
            }
        )
        //return Resource.Success(flow.flatMapConcat { it.data!!.asFlow() }.toList())
    }


    suspend fun getMovieDetails(id: Int, sessionId: String) =
        safeApiCall { movieService.getMovieDetail(id, Constants.KEY, sessionId) }

    suspend fun searchMovie(searchQuery: String, searchMoviesPageCount: Int = 1) =
        safeApiCall {
            movieService.searchMovie(searchQuery, Constants.KEY, searchMoviesPageCount)
        }

    suspend fun getFavoriteMovies(id: Int, sessionId: String, page: Int = 1) =
        networkBoundResource(
            queryLocalData = { fetchMoviesOfList(FAVORITE_MOVIES, page) },
            fetch = {
                safeApiCall { movieService.getFavorite(id, Constants.KEY, sessionId) }
            },
            saveFetchResult = { response, type ->
                val movies = response.data?.results ?: listOf()
                loadMoviesToDb(movies, type)
            },
            typeOfList = FAVORITE_MOVIES,
            queryCurrent = {
                it.data
            }
        )

    suspend fun getWatchListMovies(id: Int, sessionId: String, page: Int = 1) =
        networkBoundResource(
            queryLocalData = { fetchMoviesOfList(WATCHLIST_MOVIES, page) },
            fetch = {
                safeApiCall { movieService.getWatchList(id, Constants.KEY, sessionId) }
            },
            saveFetchResult = { response, type ->
                val movies = response.data?.results ?: listOf()
                loadMoviesToDb(movies, type)
            },
            typeOfList = WATCHLIST_MOVIES,
            queryCurrent = {
                it.data
            }
        )

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
        networkBoundResource(
            queryLocalData = { moviesDao.getGenres() },
            fetch = {
                safeApiCall{
                    movieService.getAllGenres(Constants.KEY)
                }
            },
            saveFetchResult = { response, _ ->
                moviesDao.insertGenres(response.data?.genres ?: listOf())
            },
            queryCurrent = {
                it.data?.genres
            }
        )


    private suspend fun loadMoviesToDb(movies: List<Movie>, type: String?) {
        moviesDao.insertMovies(
            MovieEntityMapper().getMovieEntityFromList(
                movies
            )
        )

        if (type != null) {
            val list = mutableListOf<MovieListMovieCrossRef>()
            movies.forEach { list.add(MovieListMovieCrossRef(type, it.id)) }
            moviesDao.insertMoviesListMoviesCrossRefs(list)
        }
    }


    private fun fetchMoviesOfList(type: String, page: Int): MovieListResponse {
        var totalPage = 1000
        var movies: List<Movie>
        try {
            movies = mapper.getSavedMovies(
                moviesDao.getMoviesOfList(type).movies, page
            )
        } catch (e: Exception) {
            Log.d("Pagination", "${e} rep")
            return MovieListResponse(0, mutableListOf())
        }

        totalPage = page

        val newMoviesList: MutableList<Movie> =
            if (movies.isNotEmpty())
                movies.toMutableList()
            else
                mutableListOf()

        return MovieListResponse(page, newMoviesList, totalPage)
    }

}


