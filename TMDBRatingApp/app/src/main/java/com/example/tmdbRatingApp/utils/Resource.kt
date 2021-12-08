package com.example.tmdbRatingApp.utils

import com.example.tmdbRatingApp.model.ErrorResponse

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(val error :ErrorResponse,data: T? = null ) : Resource<T>(data)
    class Loading<T> : Resource<T>()
}