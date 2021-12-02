package com.example.moviereviewapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.ui.view.AppTextView
import com.example.moviereviewapp.ui.view.AppTextViewBold
import com.example.moviereviewapp.utils.Constants
import com.example.moviereviewapp.utils.MyDiffUtil

class AllMovieListAdapter(
    val context: Context,
    private val clickListener: MovieListOnClickListener
) : RecyclerView.Adapter<AllMovieListAdapter.ViewHolder>() {
    var oldMovies: List<Movie> = listOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.poster_av)
        val title: AppTextViewBold = view.findViewById(R.id.tv_title_av)
        val overView: AppTextView = view.findViewById(R.id.tv_overview)
        val year: AppTextView = view.findViewById(R.id.tv_year)
        val rating: AppTextViewBold = view.findViewById(R.id.tv_rating)
        val layout: View = view.findViewById(R.id.movie_holder_av)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_holder_allmovies, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = oldMovies[position]
        holder.title.text = movie.title
        val url = Constants.IMAGE_BASE_URL + movie.poster_path
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_default_movie)
            .into(holder.poster)

        holder.layout.setOnClickListener {
            clickListener.onClick(movie , holder)
        }
        holder.overView.text = movie.overview
        holder.rating.text = movie.vote_average.toString() + " / 10"
        if (!movie.release_date.isNullOrBlank())
            holder.year.text = movie.release_date.slice(0..3)
        else
            holder.year.text = "Yet to release"
    }

    override fun getItemCount() = oldMovies.size

    fun setMoviesList(newList: List<Movie>) {
        val myDiff = MyDiffUtil(oldMovies, newList)
        val diffResult = DiffUtil.calculateDiff(myDiff)
        oldMovies = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearRecyclerView() {
        if (oldMovies.isNotEmpty())
            oldMovies = emptyList()
        notifyDataSetChanged()
    }
}

interface MovieListOnClickListener{
    fun onClick(movie: Movie, holder: AllMovieListAdapter.ViewHolder)
}
