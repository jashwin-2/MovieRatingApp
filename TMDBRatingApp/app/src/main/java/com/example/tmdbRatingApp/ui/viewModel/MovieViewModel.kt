package com.example.tmdbRatingApp.ui.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.tmdbRatingApp.db.model.Genre
import com.example.tmdbRatingApp.model.*
import com.example.tmdbRatingApp.repository.MovieRepository
import com.example.tmdbRatingApp.ui.activity.MovieDetailActivity.Companion.FAVORITE
import com.example.tmdbRatingApp.utils.Constants.NOW_PLAYING_MOVIES
import com.example.tmdbRatingApp.utils.Constants.POPULAR_MOVIES
import com.example.tmdbRatingApp.utils.Constants.TOP_RATED_MOVIES
import com.example.tmdbRatingApp.utils.Constants.UPCOMING_MOVIES
import com.example.tmdbRatingApp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val movieRepository: MovieRepository = MovieRepository(application)
    val rateMovieResponse = MutableLiveData<Response<Unit>>()
    val addOrRemoveFavoriteResponse = MutableLiveData<Response<Unit>>()
    val addOrRemoveWatchListResponse = MutableLiveData<Response<Unit>>()


    val favoriteMovies: MutableLiveData<Resource<MovieListResponse?>> = MutableLiveData()
    val watchListMovies: MutableLiveData<Resource<MovieListResponse?>> = MutableLiveData()

    val movieReviews: MutableLiveData<Resource<ReviewResponse>> = MutableLiveData()
    var reviewPageCount = 1
    var reviewResponse = ReviewResponse()

    val genreMovieList: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var genrePageCount = 1
    val genreMovieResponse = MovieResponse()

    val searchMoviesList: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var searchMoviesPageCount = 1
    var searchResponse = MovieResponse()

    var movieDetails: MutableLiveData<Resource<MovieFullDetail>> = MutableLiveData()
    var popularMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var popularMoviesPageCount = 1
    var popularMovieResponse = MovieResponse()

    var upComingMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var upComingMoviesPageCount = 1
    var upComingMovieResponse = MovieResponse()

    var topRatedMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var topRatedMoviesPageCount = 1
    var topRatedMovieResponse = MovieResponse()

    var nowPlaying: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var nowPlayingMoviesPageCount = 1
    var nowPlayingMovieResponse = MovieResponse()


    fun fetchAll() {
        getAllGenres()
        getPopularMovies()
        getNowPlayingMovies()
        getUpComingMovies()
        getTopRatedMovies()

    }

    init {
        fetchAll()
    }

    var allGenres: MutableLiveData<List<Genre>> = MutableLiveData()

    fun getAllGenres() {
        viewModelScope.launch {
            val response = movieRepository.getAllGenres()
            if (!response.data.isNullOrEmpty())
                allGenres.postValue(response.data ?: listOf())
        }
    }

    fun getPopularMovies() {
        viewModelScope.launch {
            popularMovies.postValue(Resource.Loading())
            val response = movieRepository.getMoviesList(
                POPULAR_MOVIES,
                popularMoviesPageCount
            )
            if (response is Resource.Success) {
                popularMoviesPageCount++
                popularMovies.postValue(handlePagination(response, popularMovieResponse))
            } else
                popularMovies.postValue(response)
        }
    }

    fun getUpComingMovies() {
        viewModelScope.launch {
            upComingMovies.postValue(Resource.Loading())
            val response = movieRepository.getMoviesList(
                UPCOMING_MOVIES,
                upComingMoviesPageCount
            )
            Log.d("Pagination", "vv ${response.data?.results?.size}")
            if (response is Resource.Success) {
                upComingMoviesPageCount++
                upComingMovies.postValue(handlePagination(response, upComingMovieResponse))
            } else
                upComingMovies.postValue(response)

        }
    }

    fun getTopRatedMovies() {
        Log.d("Pagination", "getTop $topRatedMoviesPageCount")

        viewModelScope.launch {
            topRatedMovies.postValue(Resource.Loading())
            val response = movieRepository.getMoviesList(
                TOP_RATED_MOVIES,
                topRatedMoviesPageCount
            )
            if (response is Resource.Success) {
                topRatedMoviesPageCount++
                topRatedMovies.postValue(handlePagination(response, topRatedMovieResponse))
            } else
                topRatedMovies.postValue(response)
        }
    }


    fun getNowPlayingMovies() {
        viewModelScope.launch {
            nowPlaying.postValue(Resource.Loading())
            val response = movieRepository.getMoviesList(
                NOW_PLAYING_MOVIES,
                nowPlayingMoviesPageCount
            )
            Log.d("Pagination", "vv ${response.data?.results?.size}")

            if (response is Resource.Success) {
                nowPlayingMoviesPageCount++
                nowPlaying.postValue(handlePagination(response, nowPlayingMovieResponse))
            } else
                nowPlaying.postValue(response)
        }
    }

    fun searchMovies(search_query: String) {
        viewModelScope.launch {
            searchMoviesList.postValue(Resource.Loading())
            val response = movieRepository.searchMovie(search_query, searchMoviesPageCount)
            if (response is Resource.Success) {
                searchMoviesPageCount++
                searchMoviesList.postValue(handlePagination(response, searchResponse))
            } else
                searchMoviesList.postValue(response)
        }
    }

    fun getMovieDetail(id: Int, sessionId: String) {
        viewModelScope.launch {
            val response = movieRepository.getMovieDetails(id, sessionId)
            Log.d("Movie", "${response.data} ")
            movieDetails.postValue(response)
        }
    }

    fun getFavoriteMovies(id: Int, sessionId: String) {
        viewModelScope.launch {
            favoriteMovies.postValue(Resource.Loading())
            val response = movieRepository.getFavoriteMovies(id, sessionId)
            favoriteMovies.postValue(response)
        }
    }

    fun getWatchListMovies(id: Int, sessionId: String) {
        viewModelScope.launch {
            watchListMovies.postValue(Resource.Loading())
            val response = movieRepository.getWatchListMovies(id, sessionId)
            watchListMovies.postValue(response)
        }
    }

    fun getMoviesListByGenre(id: Int) {
        viewModelScope.launch {
            genreMovieList.postValue(Resource.Loading())
            val response: Resource<MovieListResponse> =
                movieRepository.getMoviesByGenre(id, genrePageCount)
            if (response is Resource.Success) {
                genrePageCount++
                genreMovieList.postValue(handlePagination(response, genreMovieResponse))
            } else
                genreMovieList.postValue(response)
        }
    }

    fun addOrRemoveMovieToList(
        type: Int,
        movieId: Int,
        status: Boolean,
        sessionId: String?,
        accountId: Int
    ) {
        var response: Response<Unit>? = null

        viewModelScope.launch {
            if (type == FAVORITE) {
                val body = FavoriteBody(status, movieId, "movie")
                launch {
                    response =
                        movieRepository.addOrRemoveMovieToFavorite(body, sessionId!!, accountId)
                }.join()
                addOrRemoveFavoriteResponse.postValue(response)

            } else {
                val body = WatchListBody(movieId, "movie", status)
                launch {
                    response =
                        movieRepository.addOrRemoveMovieToWatchList(
                            body,
                            sessionId!!,
                            accountId
                        )
                }.join()
                addOrRemoveWatchListResponse.postValue(response)
            }
        }


    }


    private fun handlePagination(
        currentResponse: Resource<MovieListResponse>,
        previousResponse: MovieResponse
    ): Resource.Success<MovieListResponse> {
        if (previousResponse.movieResponse == null)
            previousResponse.movieResponse = currentResponse.data
        else {
            val oldMovies = previousResponse.movieResponse!!.results
            val newMovies = currentResponse.data?.results
            oldMovies.addAll(newMovies!!)
        }
        return Resource.Success(previousResponse.movieResponse!!)
    }

    fun getMovieReviews(movieId: Int) {
        viewModelScope.launch {
            movieReviews.postValue(Resource.Loading())
            val response = movieRepository.getMovieReviews(movieId, reviewPageCount)
            if (response is Resource.Success) {
                reviewPageCount++
                if (reviewResponse.results.isEmpty())
                    reviewResponse = response.data!!
                else {
                    val oldReviews = reviewResponse.results
                    val newReviews = response.data?.results
                    oldReviews.addAll(newReviews!!)
                }
                movieReviews.postValue(Resource.Success(reviewResponse))
            } else
                movieReviews.postValue(response)

        }
    }

    fun rateTheMovie(movieId: Int, sessionId: String, rating: Double) {
        viewModelScope.launch {
            rateMovieResponse.postValue(
                movieRepository.rateTheMovie(
                    movieId,
                    sessionId,
                    Rating(rating)
                )
            )

        }
    }

    data class MovieResponse(var movieResponse: MovieListResponse? = null)
}