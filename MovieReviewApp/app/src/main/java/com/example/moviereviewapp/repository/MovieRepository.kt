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
import networkBoundResource

class MovieRepository(application: Application) : Repository() {
    private val mapper = MovieEntityMapper()
    private val movieService: MovieService = ServiceProvider.movieService
    private val moviesDao: MoviesDao = MoviesAppDataBase.getInstance(application).movieDao

    suspend fun getMoviesList(type: String, page: Int) =

        networkBoundResource(
            queryLocalData = {
                fetchMoviesOfList(type, page)
            },
            fetch = { safeApiCall { movieService.getMoviesList(type, Constants.KEY, page) } },
            saveFetchResult = { response, _type ->
                val movies = response.data?.results ?: listOf()
                loadMoviesToDb(movies, _type)
            },
            typeOfList = type,
            queryCurrent = {
                it.data!!
            }
        )


    suspend fun getMovieDetails(id: Int, sessionId: String) =
        safeApiCall { movieService.getMovieDetail(id, Constants.KEY, sessionId) }


    suspend fun searchMovie(searchQuery: String, searchMoviesPageCount: Int = 1) =
        networkBoundResource(
            queryLocalData = {
                searchMoviesLocally(searchQuery)
            },
            fetch = {
                safeApiCall {
                    movieService.searchMovie(searchQuery, Constants.KEY, searchMoviesPageCount)
                }
            },
            queryCurrent = {
                it.data!!
            },
            saveFetchResult = { response, _ ->
                moviesDao.insertMovies(
                    mapper.getMovieEntityFromList(response.data?.results?.toList() ?: emptyList())
                )
            }
        )

    private fun searchMoviesLocally(searchQuery: String): MovieListResponse {
        val list = mapper.getMovieFromMovieEntityList(moviesDao.searchMovies(searchQuery))
        return MovieListResponse(1, list.toMutableList(), 1)
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
        networkBoundResource(
            queryLocalData = { getMoviesOfGenre(id) },
            fetch = {
                safeApiCall { movieService.getMoviesByGenre(id, Constants.KEY, genrePageCount) }
            },
            saveFetchResult = { response, _ ->
                moviesDao.insertMovies(
                    mapper.getMovieEntityFromList(response.data?.results?.toList() ?: emptyList())
                )
            },
            queryCurrent = {
                it.data!!
            }
        )


    private fun getMoviesOfGenre(id: Int): MovieListResponse {
        var list =
            mapper.getMovieFromMovieEntityList(
                moviesDao.getAllMovies().sortedByDescending { it.addedTime })
        list = list.filter { it.genre_ids.contains(id) }
        return MovieListResponse(1, list.toMutableList(), 1)
    }

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
                safeApiCall {
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
            when (type) {
                WATCHLIST_MOVIES -> moviesDao.deleteAndAddWatchList(getMoviesCrossRef(movies, type))
                FAVORITE_MOVIES -> moviesDao.deleteAndAddFavoriteList(
                    getMoviesCrossRef(
                        movies,
                        type
                    )
                )
                else -> moviesDao.insertMoviesListMoviesCrossRefs(getMoviesCrossRef(movies, type))
            }

        }
    }

    private fun getMoviesCrossRef(
        movies: List<Movie>,
        type: String
    ): MutableList<MovieListMovieCrossRef> {
        val list = mutableListOf<MovieListMovieCrossRef>()
        movies.forEach { list.add(MovieListMovieCrossRef(type, it.id)) }
        return list
    }


    private fun fetchMoviesOfList(type: String, page: Int): MovieListResponse {
        var totalPage = 1000
        val movies: List<Movie>
        try {
            movies = mapper.getSavedMovies(
                moviesDao.getMoviesOfList(type).movies
            )
        } catch (e: Exception) {
            Log.d("Pagination", "${e} rep")
            return MovieListResponse(0, mutableListOf(), 0)
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


