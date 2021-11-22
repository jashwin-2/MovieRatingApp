package com.example.moviereviewapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.GenreHolder
import com.example.moviereviewapp.ui.view.AppTextView

class GenreAdapter(val genreList: List<GenreHolder>, val clickListener: GereOnClickListener) :
    RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView : CardView = view.findViewById(R.id.cv_genre_holder)
        val imageView: ImageView = view.findViewById(R.id.iv_genre_img)
        val title: AppTextView = view.findViewById(R.id.tv_genre_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.genre_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genreList[position]
        holder.title.text = genre.title
        holder.imageView.setImageResource(genre.imageView)
        holder.cardView.setOnClickListener{
            clickListener.onClick(genre)
        }
    }

    override fun getItemCount(): Int {
        return genreList.size
    }
}

interface GereOnClickListener {
    fun onClick(genre : GenreHolder)
}