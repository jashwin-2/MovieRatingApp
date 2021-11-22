package com.example.moviereviewapp.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviereviewapp.model.MovieFullDetail
import com.example.moviereviewapp.model.MovieListResponse
import com.example.moviereviewapp.repository.MovieRepository
import com.example.moviereviewapp.utils.Constants.NOW_PLAYING_MOVIES
import com.example.moviereviewapp.utils.Constants.POPULAR_MOVIES
import com.example.moviereviewapp.utils.Constants.TOP_RATED_MOVIES
import com.example.moviereviewapp.utils.Constants.UPCOMING_MOVIES
import com.example.moviereviewapp.utils.Resource
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    val MovieRepository: MovieRepository = MovieRepository()

    val favoriteMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    val watchListMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()

    val genreMovieList: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var genrePageCount = 1
    val genreMovieResponse = MovieResponse()

    val searchMoviesList: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var searchMoviesPageCount = 1
    var searchResponse = MovieResponse()

    var movieDetails: MutableLiveData<Resource<MovieFullDetail>> = MutableLiveData()
    val popularMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var popularMoviesPageCount = 1
    var popularMovieResponse = MovieResponse()

    val upComingMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var upComingMoviesPageCount = 1
    var upComingMovieResponse = MovieResponse()

    val topRatedMovies: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var topRatedMoviesPageCount = 1
    var topRatedMovieResponse = MovieResponse()

    val nowPlaying: MutableLiveData<Resource<MovieListResponse>> = MutableLiveData()
    var nowPlayingMoviesPageCount = 1
    var nowPlayingMovieResponse = MovieResponse()

    init {
        getPopularMovies()
        getNowPlayingMovies()
        getUpComingMovies()
        getTopRatedMovies()
    }

    fun getPopularMovies() {

        viewModelScope.launch {
            popularMovies.postValue(Resource.Loading())
            val response = MovieRepository.getMoviesList(
                POPULAR_MOVIES,
                popularMoviesPageCount
            )
            if (response is Resource.Success) {
                popularMoviesPageCount++
                popularMovies.postValue(handlePagination(response, popularMovieResponse))
            } else
                nowPlaying.postValue(response)
        }
    }

    fun getUpComingMovies() {
        viewModelScope.launch {
            upComingMovies.postValue(Resource.Loading())
            val response = MovieRepository.getMoviesList(
                UPCOMING_MOVIES,
                upComingMoviesPageCount
            )
            if (response is Resource.Success) {
                upComingMoviesPageCount++
                upComingMovies.postValue(handlePagination(response, upComingMovieResponse))
            } else
                nowPlaying.postValue(response)
        }
    }

    fun getTopRatedMovies() {
        viewModelScope.launch {
            topRatedMovies.postValue(Resource.Loading())
            val response = MovieRepository.getMoviesList(
                TOP_RATED_MOVIES,
                topRatedMoviesPageCount
            )
            if (response is Resource.Success) {
                topRatedMoviesPageCount++
                topRatedMovies.postValue(handlePagination(response, topRatedMovieResponse))
            } else
                nowPlaying.postValue(response)
        }
    }

    fun getNowPlayingMovies() {
        viewModelScope.launch {
            nowPlaying.postValue(Resource.Loading())
            val response = MovieRepository.getMoviesList(
                NOW_PLAYING_MOVIES,
                nowPlayingMoviesPageCount
            )
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
            val response = MovieRepository.searchMovie(search_query, searchMoviesPageCount)
            if (response is Resource.Success) {
                searchMoviesPageCount++
                searchMoviesList.postValue(handlePagination(response, searchResponse))
            } else
                searchMoviesList.postValue(response)
        }
    }

    fun getMovieDetail(id: Int, sessionId: String) {
        viewModelScope.launch {
            val response = MovieRepository.getMovieDetails(id, sessionId)
            Log.d("Movie", "${response.data} ")
            movieDetails.postValue(response)
        }
    }

    fun getFavoriteMovies(id: Int, sessionId: String) {
        viewModelScope.launch {
            favoriteMovies.postValue(Resource.Loading())
            val response = MovieRepository.getFavoriteMovies(id, sessionId)
            favoriteMovies.postValue(response)
        }
    }

    fun getWatchListMovies(id: Int, sessionId: String) {
        viewModelScope.launch {
            watchListMovies.postValue(Resource.Loading())
            val response = MovieRepository.getWatchListMovies(id, sessionId)
            watchListMovies.postValue(response)
        }
    }

    fun getMoviesListByGenre(id: Int) {
        viewModelScope.launch {
            genreMovieList.postValue(Resource.Loading())
            val response = MovieRepository.getMoviesByGenre(id,genrePageCount)
            if (response is Resource.Success) {
                genrePageCount++
                genreMovieList.postValue(handlePagination(response, genreMovieResponse))
            } else
                genreMovieList.postValue(response)
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


    data class MovieResponse(var movieResponse: MovieListResponse? = null)
}