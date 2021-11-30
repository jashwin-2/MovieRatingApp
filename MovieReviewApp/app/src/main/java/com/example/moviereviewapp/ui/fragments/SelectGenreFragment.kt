package com.example.moviereviewapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereviewapp.R
import com.example.moviereviewapp.db.model.Genre
import com.example.moviereviewapp.ui.adapter.GenreAllClickListener
import com.example.moviereviewapp.ui.adapter.GenreListAdapter
import kotlinx.android.synthetic.main.select_genre_dialogue_fragment.view.*
import java.util.*

class SelectGenreFragment(val list : List<Genre>, private val clickListener: GenreAllClickListener) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Objects.requireNonNull(dialog)?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.select_genre_dialogue_fragment, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView_genre.apply {
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
            adapter = GenreListAdapter(list,clickListener)
        }
    }
}