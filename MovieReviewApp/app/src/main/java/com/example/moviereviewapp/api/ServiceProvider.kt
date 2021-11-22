package com.example.moviereviewapp.api

import com.example.moviereviewapp.api.service.LoginService
import com.example.moviereviewapp.api.service.MovieService
import com.example.moviereviewapp.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceProvider {
    companion object{
        private val retrofitInstance: Retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        val loginService by lazy {
            retrofitInstance.create(LoginService ::class.java)
        }
        val movieService by lazy {
            retrofitInstance.create(MovieService ::class.java)
        }
    }
}