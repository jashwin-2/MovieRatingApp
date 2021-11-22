package com.example.moviereviewapp.repository

import android.util.Log
import com.example.moviereviewapp.model.ErrorResponse
import com.example.moviereviewapp.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class Repository {

    companion object {
        const val NULL_BODY_ERROR_CODE = -1
    }


    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            val nullBodyError by lazy {
                Resource.Error<T>(
                    ErrorResponse(
                        NULL_BODY_ERROR_CODE, "Body is Null", false
                    )
                )
            }

            val response: Response<T> = apiCall.invoke()

            if (response.errorBody()?.charStream()!=null) {
                val errorB = convertErrorBody(response)
                Log.d("Error", "Error ")
                return@withContext Resource.Error(errorB,null)
            }

            if (response.isSuccessful) {

                val body = response.body() ?: return@withContext nullBodyError
                Resource.Success(body)
            } else {
                val error = convertErrorBody(response)
                val body = response.body() ?: return@withContext nullBodyError
                Resource.Error(error, body)
            }
        }
    }


    private fun convertErrorBody(response: Response<*>): ErrorResponse {
        val gson = Gson()
        return gson.fromJson(
            response.errorBody()!!.charStream(),
            ErrorResponse::class.java
        )
    }
}