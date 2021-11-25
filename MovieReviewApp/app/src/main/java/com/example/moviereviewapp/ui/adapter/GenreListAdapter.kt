package com.example.moviereviewapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Genre
import com.example.moviereviewapp.ui.view.AppTextView
import kotlinx.android.synthetic.main.genre_title_holder.view.*

class GenreListAdapter(val list: List<Genre>, private val listener: GenreAllClickListener) :
    RecyclerView.Adapter<GenreListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppTextView = view.tv_genre_name
        val holder: CardView = view.genre_title_holder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.genre_title_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = list[position].name
        holder.holder.setOnClickListener {
            listener.onClick(list[position].id, list[position].name)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface GenreAllClickListener {
    fun onClick(id: Int, name: String)
}