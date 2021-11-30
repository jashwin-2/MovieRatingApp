package com.example.moviereviewapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.GenreHolder
import com.example.moviereviewapp.ui.activity.AllMoviesActivity
import com.example.moviereviewapp.ui.adapter.GenreAdapter
import com.example.moviereviewapp.ui.adapter.GenreAllClickListener
import com.example.moviereviewapp.ui.adapter.GereOnClickListener
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search_home.view.*

class SearchHomeFragment : Fragment(R.layout.fragment_search_home), GereOnClickListener,
    GenreAllClickListener {
    var allGenreFragment: SelectGenreFragment? = null
    lateinit var movieViewModel: MovieViewModel

    companion object {
        const val GENRE_ID = "genre_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_home, search_fragment_container, false)

        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MovieViewModel::class.java].also {
            movieViewModel = it
        }

        setRecyclerView(view)
        addObservers()
        return view
    }

    private fun addObservers() {
        movieViewModel.allGenres.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                allGenreFragment = SelectGenreFragment(movieViewModel.allGenres.value!!, this)
        }
    }


    private fun setRecyclerView(view: View) {
        //Home
        view.rv_genre.adapter = GenreAdapter(getGenres(), this)
        GridLayoutManager(activity, 3).apply {
            view.rv_genre.layoutManager = this
        }

    }

    private fun getGenres() = listOf(
        GenreHolder("Action", R.drawable.ic_genre_action, 28),
        GenreHolder("Comedy", R.drawable.ic_genre_comedy, 35),
        GenreHolder("Fantasy", R.drawable.ic_genre_fantacy, 14),
        GenreHolder("Horror", R.drawable.ic_genre_horror, 27),
        GenreHolder("Music", R.drawable.ic_genre_music, 10402),
        GenreHolder("Romance", R.drawable.ic_genre_romance, 10749),
        GenreHolder("Sci-fic", R.drawable.ic_genre_sci_fic, 878),
        GenreHolder("View More", R.drawable.ic_select_more, 0),
    )

    override fun onClick(genre: GenreHolder) {
        if (genre.id == 0)
            if (allGenreFragment != null)
                allGenreFragment?.show(childFragmentManager, "All Genres")
            else
                Toast.makeText(activity, "Need Internet to load data", Toast.LENGTH_SHORT).show()
        else
            startActivity(Intent(activity, AllMoviesActivity::class.java).apply {
                putExtra(GENRE_ID, genre.id)
                putExtra(AllMoviesActivity.SELECTED_TYPE, AllMoviesActivity.GENRE_MOVIES)
                putExtra(AllMoviesActivity.SELECTED_TITLE, genre.title)
            })
    }

    override fun onClick(id: Int, name: String) {
        allGenreFragment?.dismiss()
        startActivity(Intent(activity, AllMoviesActivity::class.java).apply {
            putExtra(GENRE_ID, id)
            putExtra(AllMoviesActivity.SELECTED_TYPE, AllMoviesActivity.GENRE_MOVIES)
            putExtra(AllMoviesActivity.SELECTED_TITLE, name)
        })


    }
}