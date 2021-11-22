package com.example.moviereviewapp.repository

import com.example.moviereviewapp.api.service.LoginService
import com.example.moviereviewapp.api.ServiceProvider
import com.example.moviereviewapp.model.LoginRequest
import com.example.moviereviewapp.utils.Constants
import okhttp3.RequestBody

class LoginRepository : Repository() {
    private val loginService: LoginService = ServiceProvider.loginService

    suspend fun getAuthenticationToken() =
        safeApiCall {loginService.getAuthenticationToken(Constants.KEY)}

    suspend fun getSessionId(params: RequestBody) =
        safeApiCall {loginService.getSessionId(Constants.KEY, params)}

     suspend fun getAccount(sessionId: String) =
       safeApiCall {loginService.getAccount(Constants.KEY, sessionId)}

    suspend fun login(loginRequest: LoginRequest) =
        safeApiCall { loginService.login(Constants.KEY, loginRequest) }


}