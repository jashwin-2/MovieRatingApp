package com.example.tmdbRatingApp.api.service

import com.example.tmdbRatingApp.model.LoginResponse
import com.example.tmdbRatingApp.model.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface LoginService {

    @GET("3/authentication/token/new")
    suspend fun getAuthenticationToken(
        @Query("api_key") key: String
    ): Response<AuthToken>

    @POST("3/authentication/session/new")
    suspend fun getSessionId(
        @Query("api_key") key: String,
        @Body reqToken: RequestBody
    ): Response<SessionId>

    @GET("3/account")
    suspend fun getAccount(
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String
    ): Response<Account>

   @GET("3/account/{Acc_Id}/watchlist/movies")
   suspend fun getWatchList(
       @Path("Acc_Id") id: Int,
       @Query("api_key") key: String,
       @Query("session_id") sessionId: String
   ): Response<MovieListResponse>

    @POST("3/authentication/token/validate_with_login")
    suspend fun login(
        @Query("api_key") key: String,
        @Body loginReq: LoginRequest
    ) : Response<LoginResponse>
}