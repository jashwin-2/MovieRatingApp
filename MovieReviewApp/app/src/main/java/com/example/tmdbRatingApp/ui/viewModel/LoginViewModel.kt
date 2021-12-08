package com.example.tmdbRatingApp.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbRatingApp.model.*
import com.example.tmdbRatingApp.repository.LoginRepository
import com.example.tmdbRatingApp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val loginRepository = LoginRepository()
    val sessionId: MutableLiveData<Resource<SessionId>> = MutableLiveData()

    val account: MutableLiveData<Resource<Account>> = MutableLiveData()
    val loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()


    fun login(userName: String, password: String) {
        viewModelScope.launch {

            launch {
                val token =
                    withContext(Dispatchers.IO) { getRequestToken() } ?: return@launch
                val loginRequest = LoginRequest(userName, password, token.request_token)
                val response = loginRepository.login(loginRequest)
                if (response is Resource.Success) {
                    if (response.data!!.success) {
                        withContext(Dispatchers.IO) {
                            getSessionId(token, response)
                        }

                    }

                } else
                    loginResponse.postValue(response)
            }
        }


    }

    private suspend fun getRequestToken(): AuthToken? {

        loginResponse.postValue(Resource.Loading())
        val response = loginRepository.getAuthenticationToken()
        return if (response is Resource.Success)
            response.data
        else {
            loginResponse.postValue(
                Resource.Error((response as Resource.Error).error)
            )
            null
        }

    }

    private suspend fun getSessionId(token: AuthToken, loginResponse: Resource<LoginResponse>) {
        viewModelScope.launch {
            val response =
                loginRepository.getSessionId(createJsonRequestBody("request_token" to token.request_token))
            sessionId.postValue(response)
            if (response is Resource.Success) {
                val accResponse = withContext(Dispatchers.IO) {
                    getAccount(response.data!!.session_id)
                }
                if (accResponse is Resource.Success<*>)
                    this@LoginViewModel.loginResponse.postValue(loginResponse)
                else
                    this@LoginViewModel.loginResponse.postValue(Resource.Error((response as Resource.Error<*>).error))
            } else if (response is Resource.Error)
                this@LoginViewModel.loginResponse.postValue(Resource.Error(response.error))
        }
    }

    suspend fun getAccount(sessionId: String): Resource<Account> {
        account.postValue(Resource.Loading())
        val response = loginRepository.getAccount(sessionId)
        account.postValue(response)
        return response

    }

    private fun createJsonRequestBody(vararg params: Pair<String, String>) =
        RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(mapOf(*params)).toString()
        )

}
