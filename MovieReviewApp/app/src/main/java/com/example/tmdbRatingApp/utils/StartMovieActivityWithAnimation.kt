package com.example.tmdbRatingApp.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.example.tmdbRatingApp.model.Movie
import com.example.tmdbRatingApp.ui.activity.MovieDetailActivity
import com.example.tmdbRatingApp.ui.fragments.HomeFragment


fun startMovieDetailActivity(activity: Activity, movie: Movie, poster: ImageView) =
    Intent(activity, MovieDetailActivity::class.java).apply {
        putExtra(HomeFragment.MOVIE_ID, movie.id)
        putExtra(MovieDetailActivity.POSTER_PATH, movie.poster_path)
        poster.transitionName = "iv_movie_poster${movie.id}"

        startActivity(activity,this,
            ActivityOptions.makeSceneTransitionAnimation(activity, poster, poster.transitionName)
                .toBundle()
        )
    }
