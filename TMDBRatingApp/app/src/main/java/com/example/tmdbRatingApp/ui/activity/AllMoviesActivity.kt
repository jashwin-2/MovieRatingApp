package com.example.tmdbRatingApp.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.model.Movie
import com.example.tmdbRatingApp.model.MovieListResponse
import com.example.tmdbRatingApp.ui.adapter.AllMovieListAdapter
import com.example.tmdbRatingApp.ui.adapter.MovieListOnClickListener
import com.example.tmdbRatingApp.ui.fragments.SearchHomeFragment.Companion.GENRE_ID
import com.example.tmdbRatingApp.ui.viewModel.MovieViewModel
import com.example.tmdbRatingApp.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_all_movies.*
import kotlinx.android.synthetic.main.toolbar.view.*

class AllMoviesActivity : AppCompatActivity(), MovieListOnClickListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var adapter: AllMovieListAdapter
    lateinit var snackbar: Snackbar
    var type: Int = 0
    var genreId = 0
    lateinit var recyclerView: RecyclerView
    lateinit var myScrollListener: MyScrollListener
    lateinit var toolbar: Toolbar
    var comingFromNoInternet = false
    var loadingPagination = false
    var firstLoad = true


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
        initializeSnackBar()
        addNetworkStateObserver()

    }

    private fun initializeSnackBar() {
        snackbar = Snackbar.make(
            this.layout_all_movies,
            "No Connection",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setBackgroundTint(resources.getColor(R.color.snack_bar_bg))
        }
    }

    private fun addNetworkStateObserver() {

        NetworkConnectionLiveData(this).observe(this) {
            if (!it)
                showNoConnectionSnackBar()
            else if (it && comingFromNoInternet) {
                showBackToOnlineSnackBar()
                if (loadingPagination)
                    callCorrespondingMovieType(type)
                else
                    refresh()
            }
        }
    }

    private fun showBackToOnlineSnackBar() {
        snackbar.dismiss()
        Snackbar.make(
            this.layout_all_movies,
            "Back Online",
            Snackbar.LENGTH_SHORT
        ).apply {
            setBackgroundTint(resources.getColor(R.color.green))
        }.show()
    }

    private fun refresh() {
        callCorrespondingMovieType(type)
        adapter.clearRecyclerView()
        recyclerView.scrollToPosition(0)
        myScrollListener.isLastPage = false
        firstLoad = true
    }

    private fun showNoConnectionSnackBar() {
        snackbar.show()
        comingFromNoInternet = true

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
            callCorrespondingMovieType(it)
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
        data.observe(this) {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    val totalPage = it.data!!.total_pages
                    myScrollListener.isLastPage = it.data.page == totalPage
                    firstLoad = false
                    main_progress_bar.visibility = View.GONE
                    it.data.let { it1 ->
                        adapter.setMoviesList(it1.results.toList())
                    }
                }
                is Resource.Error -> {
                    val movies = it.data?.results ?: listOf()

                    if (firstLoad) {
                        myScrollListener.isLastPage = true
                        if (movies.isNotEmpty())
                            loadReceivedStoredData(movies)
                    } else
                        showLoadOfflineDataSnackBar(movies)

                    if (!isNetworkAvailable(this) && !comingFromNoInternet)
                        showNoConnectionSnackBar()

                }
                is Resource.Loading -> {
                    if (!firstLoad)
                        showProgressBar()
                    else
                        main_progress_bar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showLoadOfflineDataSnackBar(movies: List<Movie>) {
        loadingPagination = true
        Snackbar.make(
            this.layout_all_movies,
            "No Connection",
            Snackbar.LENGTH_INDEFINITE
        ).apply {

            setAction("Load Old Data") {
                loadingPagination = false
                myScrollListener.isLastPage = true
                adapter.clearRecyclerView()
                recyclerView.scrollToPosition(0)
                loadReceivedStoredData(movies)

            }
            setBackgroundTint(resources.getColor(R.color.snack_bar_bg))
        }.show()
    }

    private fun loadReceivedStoredData(movies: List<Movie>) {
        hideProgressBar()
        main_progress_bar.visibility = View.GONE
        adapter.setMoviesList(movies.toList())
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


    private fun callCorrespondingMovieType(_type: Int) {
        when (_type) {
            NOW_PLAYING -> movieViewModel.getNowPlayingMovies()
            POPULAR -> movieViewModel.getPopularMovies()
            UPCOMING -> movieViewModel.getUpComingMovies()
            TOP_RATED -> movieViewModel.getTopRatedMovies()
            GENRE_MOVIES -> movieViewModel.getMoviesListByGenre(genreId)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isNetworkAvailable(this))
            showNoConnectionSnackBar()
        else if (comingFromNoInternet) {
            refresh()
            snackbar.dismiss()
            comingFromNoInternet = false
        }
    }

    override fun onClick(movie: Movie, holder: AllMovieListAdapter.ViewHolder) {
        startMovieDetailActivity(this, movie, holder.poster)
    }

}
