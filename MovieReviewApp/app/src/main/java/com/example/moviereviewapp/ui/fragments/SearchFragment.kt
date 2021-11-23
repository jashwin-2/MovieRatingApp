package com.example.moviereviewapp.ui.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.GenreHolder
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.ui.activity.AllMoviesActivity
import com.example.moviereviewapp.ui.activity.AllMoviesActivity.Companion.SEARCHING
import com.example.moviereviewapp.ui.activity.MovieDetailActivity
import com.example.moviereviewapp.ui.activity.ProfileActivity
import com.example.moviereviewapp.ui.adapter.*
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.MyScrollListener
import com.example.moviereviewapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_search_home.view.*
import kotlinx.android.synthetic.main.fragment_search_result.view.*
import kotlinx.android.synthetic.main.search_toolbar.view.*

class SearchFragment : Fragment(R.layout.fragment_search), SearchView.OnQueryTextListener,
    MovieListAdapter.MovieOnClickListener, GereOnClickListener, GenreAllClickListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var allGenreFragment: SelectGenreFragment
    lateinit var searchLayout: View
    lateinit var myScrollListener: MyScrollListener
    lateinit var adapter: AllMovieListAdapter
    private var queryText: String = " "
    lateinit var paginationProgressBar: ProgressBar
    lateinit var searchHome: LinearLayout
    lateinit var searchResult: ConstraintLayout

    companion object {

        const val GENRE_ID = "genre_id"
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        addObserver()
        searchLayout = view.toolbar
        searchLayout.sv_search.setOnQueryTextListener(this)

        searchHome = view.findViewById(R.id.layout_search_home)
        searchResult = view.findViewById(R.id.layout_search_result)

        paginationProgressBar = searchResult.progress_circular_search

        searchLayout.iv_profile_search.setOnClickListener {
            Intent(activity, ProfileActivity::class.java).apply { startActivity(this) }
        }


        setRecyclerView(view)
        return view
    }

    private fun setUpScrollListener(query: String) {
        myScrollListener = MyScrollListener(SEARCHING) {
            if (it == SEARCHING)
                movieViewModel.searchMovies(query)
        }
    }

    private fun addObserver() {
        movieViewModel.searchMoviesList.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    adapter.setMoviesList(it.data?.results?.toList() ?: listOf())
                    val totalPage = it.data!!.total_pages
                    myScrollListener.isLastPage = it.data.page == totalPage

                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(
                        context,
                        it.error.status_message,
                        Toast.LENGTH_LONG
                    ).show()

                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        movieViewModel.allGenres.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                allGenreFragment = SelectGenreFragment(movieViewModel.allGenres.value!!, this)
        }
    }

    private fun setRecyclerView(view: View) {
        //Home
        searchHome.rv_genre.adapter = GenreAdapter(getGenres(), this)
        GridLayoutManager(activity, 3).apply {
            searchHome.rv_genre.layoutManager = this
        }
        //Search Result
        adapter = AllMovieListAdapter(requireContext(), this)
        view.rv_result.adapter = adapter
        view.rv_result.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

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

    override fun onQueryTextSubmit(queryString: String?): Boolean {
        searchResult.visibility = View.VISIBLE
        searchHome.visibility = View.GONE
        if (!queryString.isNullOrBlank() && queryString != queryText) {
            Log.d("Search", "Called")
            queryText = queryString
            movieViewModel.searchMoviesPageCount = 1
            movieViewModel.searchResponse = MovieViewModel.MovieResponse()
            setUpScrollListener(queryString)
            movieViewModel.searchMovies(queryString)
            requireView().rv_result.addOnScrollListener(myScrollListener)
        }
        return true
    }

    override fun onQueryTextChange(string: String?): Boolean {
        if (string?.isBlank() == true) {
            searchHome.visibility = View.VISIBLE
            searchResult.visibility = View.GONE
        }
        return true
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        myScrollListener.isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        myScrollListener.isLoading = true
    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(activity, MovieDetailActivity::class.java).apply {
            putExtra(HomeFragment.MOVIE_ID, movie.id)
        })
    }

    override fun onClick(genre: GenreHolder) {
        if (genre.id == 0)
            allGenreFragment.show(childFragmentManager, "All Genres")
        else
            startActivity(Intent(activity, AllMoviesActivity::class.java).apply {
                putExtra(GENRE_ID, genre.id)
                putExtra(AllMoviesActivity.SELECTED_TYPE, AllMoviesActivity.GENRE_MOVIES)
                putExtra(AllMoviesActivity.SELECTED_TITLE, genre.title)
            })
    }

    override fun onClick(id: Int, name: String) {
        allGenreFragment.dismiss()
        startActivity(Intent(activity, AllMoviesActivity::class.java).apply {
            putExtra(GENRE_ID, id)
            putExtra(AllMoviesActivity.SELECTED_TYPE, AllMoviesActivity.GENRE_MOVIES)
            putExtra(AllMoviesActivity.SELECTED_TITLE, name)
        })
    }
}