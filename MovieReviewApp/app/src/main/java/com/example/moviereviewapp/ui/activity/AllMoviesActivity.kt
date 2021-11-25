package com.example.moviereviewapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.model.MovieListResponse
import com.example.moviereviewapp.ui.activity.MovieDetailActivity
import com.example.moviereviewapp.ui.adapter.AllMovieListAdapter
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.ui.fragments.HomeFragment
import com.example.moviereviewapp.ui.fragments.SearchHomeFragment.Companion.GENRE_ID
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.MyScrollListener
import com.example.moviereviewapp.utils.Resource
import kotlinx.android.synthetic.main.activity_all_movies.*
import kotlinx.android.synthetic.main.toolbar.view.*

class AllMoviesActivity : AppCompatActivity(), MovieListAdapter.MovieOnClickListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var adapter: AllMovieListAdapter
    var type: Int = 0
    var genreId = 0
    lateinit var recyclerView: RecyclerView
    lateinit var myScrollListener: MyScrollListener
    lateinit var toolbar: Toolbar

    companion object {
        const val SELECTED_TYPE = "type"
        const val SELECTED_TITLE = "title"
        const val POPULAR = 1
        const val TOP_RATED = 2
        const val NOW_PLAYING = 3
        const val UPCOMING = 4
        const val GENRE_MOVIES = 6
        const val SEARCHING = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_movies)


        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MovieViewModel::class.java].also {
            movieViewModel = it
        }

        toolbar = allmovies_toolbar.toolbar_custom
        type = intent.getIntExtra(SELECTED_TYPE, 1)

        val data = getType(type)
        if (type == GENRE_MOVIES) {
            genreId = intent.getIntExtra(GENRE_ID, 0)
            movieViewModel.getMoviesListByGenre(genreId)
        }

        val title = intent.getStringExtra(SELECTED_TITLE)
        setUpToolBar(title)
        addObserver(data)
        setRecyclerView()


    }

    private fun setUpToolBar(newTitle: String?) = toolbar.apply {
        title = newTitle
        setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setRecyclerView() {
        recyclerView = rv_all_Movies

        LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
            recyclerView.layoutManager = this
        }

        adapter = AllMovieListAdapter(this, this)
        recyclerView.adapter = adapter

        myScrollListener = MyScrollListener(type) {
            when (type) {
                NOW_PLAYING -> movieViewModel.getNowPlayingMovies()
                POPULAR -> movieViewModel.getPopularMovies()
                UPCOMING -> movieViewModel.getUpComingMovies()
                TOP_RATED -> movieViewModel.getTopRatedMovies()
                GENRE_MOVIES -> movieViewModel.getMoviesListByGenre(genreId)
            }
        }
        recyclerView.addOnScrollListener(myScrollListener)
    }

    private fun getType(value: Int): MutableLiveData<Resource<MovieListResponse>> {
        return when (value) {
            POPULAR -> movieViewModel.popularMovies
            TOP_RATED -> movieViewModel.topRatedMovies
            NOW_PLAYING -> movieViewModel.nowPlaying
            UPCOMING -> movieViewModel.upComingMovies
            GENRE_MOVIES -> movieViewModel.genreMovieList
            else -> movieViewModel.popularMovies
        }
    }

    private fun hideProgressBar() {
        pagination_prograss_bar.visibility = View.INVISIBLE
        myScrollListener.isLoading = false
    }

    private fun showProgressBar() {
        pagination_prograss_bar.visibility = View.VISIBLE
        myScrollListener.isLoading = true
    }

    private fun addObserver(data: MutableLiveData<Resource<MovieListResponse>>) {
        var firstLoad = true
        data.observe(this) {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    firstLoad = false
                    main_progress_bar.visibility = View.GONE
                    it.data?.let { it1 ->
                        adapter.setMoviesList(it1.results.toList())
                    }
                    val totalPage = it.data!!.total_pages
                    myScrollListener.isLastPage = it.data.page == totalPage
                }
                is Resource.Error -> {
                    hideProgressBar()
                    main_progress_bar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        it.error.status_message,
                        Toast.LENGTH_LONG
                    ).show()

                }
                is Resource.Loading -> {
                    Log.d("Genre", "Called")
                    if (!firstLoad)
                        showProgressBar()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                onBackPressed()
                true
            }
            else -> false
        }
    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(this, MovieDetailActivity::class.java).apply {
            putExtra(HomeFragment.MOVIE_ID, movie.id)
        })
    }
}
