package com.example.moviereviewapp.api.service

import com.example.moviereviewapp.model.MovieFullDetail
import com.example.moviereviewapp.model.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("3/movie/{Type}")
    suspend fun getMoviesList(
        @Path("Type") type: String,
        @Query("api_key") key: String,
        @Query("page") page: Int
    ): Response<MovieListResponse>

    @GET("3/movie/{MovieId}")
    suspend fun getMovieDetail(
        @Path("MovieId") movieId: Int,
        @Query("api_key") key: String,
        @Query("session_id") id: String,
        @Query("append_to_response") query: String = "videos,credits,account_states,recommendations",
        @Query("language") language: String = "en-US"
    ): Response<MovieFullDetail>

    @GET("3/search/movie")
    suspend fun searchMovie(
        @Query("query") search_query: String,
        @Query("api_key") key: String,
        @Query("page") page: Int
    ): Response<MovieListResponse>

    @GET("/3/account/{account_id}/favorite/movies")
    suspend fun getFavorite(
        @Path("account_id") id: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Query("sort_by") sortBy: String = "created_at.desc"
    ): Response<MovieListResponse>

    @GET("/3/account/{account_id}/watchlist/movies")
    suspend fun getWatchList(
        @Path("account_id") id: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Query("sort_by") sortBy: String = "created_at.desc"
    ): Response<MovieListResponse>


    @GET("/3/discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") id: Int,
        @Query("api_key") key: String,
        @Query("page") genrePageCount: Int
    ): Response<MovieListResponse>

}

