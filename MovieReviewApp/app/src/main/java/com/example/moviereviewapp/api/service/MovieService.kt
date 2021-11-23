package com.example.moviereviewapp.api.service

import com.example.moviereviewapp.model.*
import retrofit2.Response
import retrofit2.http.*
import javax.sql.StatementEvent

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


    @POST("3/account/{account_id}/favorite")
    suspend fun addOrRemoveMovieToFavorite(
        @Path("account_id") id: Int,
        @Body favoriteBody: FavoriteBody,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
    ) : Response<Unit>

    @POST("3/account/{account_id}/watchlist")
    suspend fun addOrRemoveMovieToWatchList(
        @Path("account_id") id: Int,
        @Body watchListBody: WatchListBody,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String
    ):Response<Unit>?

    @POST("3/movie/{movie_id}/rating")
    suspend fun rateTheMovie(
        @Path("movie_id") movieId : Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Body rating: Rating
    ) : Response<Unit>?

    @GET("3/genre/movie/list")
    suspend fun getAllGenres(
        @Query("api_key") key: String,
        @Query("language") lang: String = "language=en-US"
        ) : Response<GenreListResponse>

}

