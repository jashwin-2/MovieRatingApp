package com.example.moviereviewapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.ui.view.AppTextView
import com.example.moviereviewapp.utils.Constants

class MovieListAdapter(
    val context: Context,
    private val clickListener: MovieOnClickListener,
    val type: Int = 0
) : RecyclerView.Adapter<MovieListAdapter.MovieHolder>() {
    var oldMovieList: List<Movie> = listOf()

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
        holder.releaserYear.text = movie.release_date?.slice(0..3) ?: "not released"
        val url = Constants.IMAGE_BASE_URL + movie.poster_path
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_default_movie)
            .into(holder.poster)
        holder.layout.setOnClickListener {
            clickListener.onClick(oldMovieList[position] , holder)
        }

    }

    override fun getItemCount(): Int {
        return oldMovieList.size
    }

    fun setMoviesList(list: List<Movie>) {
        oldMovieList = list
        notifyDataSetChanged()
    }

    interface MovieOnClickListener {
        fun onClick(movie: Movie, holder: MovieHolder)
    }
}