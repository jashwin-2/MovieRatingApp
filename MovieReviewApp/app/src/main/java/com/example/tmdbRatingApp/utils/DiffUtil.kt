package com.example.tmdbRatingApp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.tmdbRatingApp.model.Movie

class MyDiffUtil(
    private var oldMovies : List<Movie>,
    private var newMovies : List<Movie>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return  oldMovies.size
    }

    override fun getNewListSize(): Int {
      return newMovies.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldMovies[oldItemPosition].id == newMovies[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMovies[oldItemPosition] === newMovies[newItemPosition]
    }
}