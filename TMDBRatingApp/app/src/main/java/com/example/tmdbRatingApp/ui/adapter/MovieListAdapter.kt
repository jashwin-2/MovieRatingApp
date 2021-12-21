package com.example.tmdbRatingApp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.extensions.loadImage
import com.example.tmdbRatingApp.model.Movie
import com.example.tmdbRatingApp.ui.view.AppTextView
import com.example.tmdbRatingApp.utils.Constants
import com.example.tmdbRatingApp.utils.MyDiffUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MovieListAdapter(
    val context: Context,
    private val clickListener: MovieOnClickListener,
    val type: Int = 0,
) : RecyclerView.Adapter<MovieListAdapter.MovieHolder>() {
    private var oldMovieList: List<Movie> = listOf()
    var recyclerView: RecyclerView? = null

    companion object {
        const val POPULAR = 1
        const val TOP_RATED = 2
        const val UPCOMING = 3
        const val NOW_PLAYING = 4
    }

    inner class MovieHolder(movieView: View) : RecyclerView.ViewHolder(movieView) {
        val layout: View = movieView.findViewById(R.id.layout_movie_holder)
        val poster: ImageView = movieView.findViewById(R.id.moviePoster)
        val title: AppTextView = movieView.findViewById(R.id.movieName)
        val rating: AppTextView = movieView.findViewById(R.id.rating)
        val releaserYear: AppTextView = movieView.findViewById(R.id.release_year)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_holder, parent, false)
        return MovieHolder(view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = oldMovieList[position]
        holder.title.text = movie.title
        holder.rating.text = movie.vote_average.toString()

        holder.releaserYear.text =
            if (!movie.release_date.isNullOrBlank()) movie.release_date.slice(0..3) else "not released"
        val url = Constants.IMAGE_BASE_URL + movie.poster_path
//        Glide.with(context)
//            .load(url)
//            .placeholder(R.drawable.ic_default_movie)
//            .into(holder.poster)
        (context as AppCompatActivity).loadImage(url, holder.poster, R.drawable.ic_default_movie)

        holder.layout.setOnClickListener {
            clickListener.onClick(oldMovieList[position], holder)
        }

    }

    override fun getItemCount(): Int {
        return oldMovieList.size
    }

    fun setMoviesList(newList: List<Movie>) {
        if (isBothListsAreDifferent(newList)) {
            oldMovieList = newList
            notifyDataSetChanged()
            recyclerView?.scrollToPosition(0)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    interface MovieOnClickListener {
        fun onClick(movie: Movie, holder: MovieHolder)
    }

    private fun isBothListsAreDifferent(newList: List<Movie>): Boolean {
        return !(newList.size <= oldMovieList.size && oldMovieList.containsAll(
            newList
        ))
    }
}