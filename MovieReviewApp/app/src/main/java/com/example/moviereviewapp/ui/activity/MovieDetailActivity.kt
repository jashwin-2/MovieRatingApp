package com.example.moviereviewapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereviewapp.R
import com.example.moviereviewapp.extensions.loadImage
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.model.MovieFullDetail
import com.example.moviereviewapp.ui.adapter.CastAdapter
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.ui.adapter.ProductionCompanyAdapter
import com.example.moviereviewapp.ui.fragments.HomeFragment.Companion.MOVIE_ID
import com.example.moviereviewapp.ui.fragments.RatingFragment
import com.example.moviereviewapp.ui.fragments.RatingListener
import com.example.moviereviewapp.ui.fragments.SearchHomeFragment.Companion.GENRE_ID
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_all_movies.*
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.cast_layout.*
import kotlinx.android.synthetic.main.production_company_layout.*
import kotlinx.android.synthetic.main.search_toolbar.view.*
import kotlinx.android.synthetic.main.similar_movies_layout.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MovieDetailActivity : AppCompatActivity(), MovieListAdapter.MovieOnClickListener,
    RatingListener {

    var comingFromNoInternet = false
    var movieId = 0
    lateinit var snackbar: Snackbar
    lateinit var movieDetailLayout: NestedScrollView
    lateinit var movieViewModel: MovieViewModel
    lateinit var movie: MovieFullDetail
    lateinit var toolbar: Toolbar
    var sessionId: String? = null
    var accountId = 0

    companion object {
        const val FAVORITE = 1
        const val WATCHLIST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        // movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MovieViewModel::class.java].also {
            movieViewModel = it
        }
        toolbar = movie_detail_toolbar.toolbar_custom
        movieDetailLayout = this.findViewById(R.id.nestedScrollView2)
        movieDetailLayout.visibility = View.GONE
        setToolBar("Movie Name")
        setObserver()

        movieId = intent.getIntExtra(MOVIE_ID, 0)
        SessionManager(this).apply {
            sessionId = fetchAuthToken()
            accountId = fetchAccId()
        }

        initializeSnackBar()
        addNetworkStateObserver()
        movieViewModel.getMovieDetail(movieId, sessionId!!)

    }

    private fun setObserver() {
        movieViewModel.movieDetails.observe(this) {
            when (it) {
                is Resource.Success -> {
                    progress_circular_movie_detail.visibility = View.GONE
                    movie = it.data!!
                    setToolBar(movie.title)
                    setActivity()
                    movieDetailLayout.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    progress_circular_movie_detail.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    if (!isNetworkAvailable(this) && !comingFromNoInternet)
                        showNoConnectionSnackBar()
                    else
                        Toast.makeText(
                            this,
                            it.error.status_message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                }
            }
        }
    }

    private fun setToolBar(newTitle: String?) = toolbar.apply {
        title = newTitle
        setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setActivity() {
        loadPoster()
        setGenreGroup()
        initializeTrailer()
        setCastRv()
        setSimilarMoviesRv()
        setProductionCompanyRv()
        setFavoriteButton()
        setWatchListButton()
        setRatingListener()
        val fragment = RatingFragment(this)

        iv_rate_the_movie.setOnClickListener {
            fragment.show(supportFragmentManager, "Rating")
        }
        tv_rate_the_movie.setOnClickListener {
            fragment.show(supportFragmentManager, "Rating")
        }


    }

    private fun setRatingListener() {
        movieViewModel.rateMovieResponse.observe(this) {
            if (it.isSuccessful)
                Toast.makeText(this, "Movie Rated Successfully", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this, it.errorBody().toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun setFavoriteButton() {
        if (movie.account_states.favorite)
            btn_favorite.setBackgroundResource(R.drawable.ic_favorite_filled)

        btn_favorite.setOnClickListener {
            if (isNetworkAvailable(this))
                movieViewModel.addOrRemoveMovieToList(
                    FAVORITE,
                    movie.id,
                    !movie.account_states.favorite,
                    sessionId,
                    accountId
                )
            else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
        }
        setFavoriteObserver()
    }

    private fun setWatchListButton() {

        if (movie.account_states.watchlist)
            btn_watch_list.setBackgroundResource(R.drawable.ic_watchlist_filled)
        btn_watch_list.setOnClickListener {
            if (isNetworkAvailable(this))
                movieViewModel.addOrRemoveMovieToList(
                    WATCHLIST,
                    movie.id,
                    !movie.account_states.watchlist,
                    sessionId,
                    accountId
                )
            else
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()

        }

        setWatchListObserver()

    }

    private fun setWatchListObserver() {
        movieViewModel.addOrRemoveWatchListResponse.observe(this) {
            if (it.isSuccessful) {
                val currentStatus = movie.account_states.watchlist
                if (!currentStatus) {
                    movie.account_states.watchlist = !currentStatus
                    btn_watch_list.setBackgroundResource(R.drawable.ic_watchlist_filled)
                    Toast.makeText(this, "Added to WatchList", Toast.LENGTH_SHORT).show()
                } else {
                    btn_watch_list.setBackgroundResource(R.drawable.ic_watchlist_grey)
                    movie.account_states.watchlist = !currentStatus
                    Toast.makeText(this, "Removed from Watchlist", Toast.LENGTH_SHORT).show()
                }
            } else
                Toast.makeText(
                    this,
                    it.errorBody()?.charStream().toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()
        }
    }

    private fun setFavoriteObserver() {
        movieViewModel.addOrRemoveFavoriteResponse.observe(this) {
            if (it.isSuccessful) {
                val currentStatus = movie.account_states.favorite

                if (!currentStatus) {
                    movie.account_states.favorite = !currentStatus
                    btn_favorite.setBackgroundResource(R.drawable.ic_favorite_filled)
                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                } else {
                    btn_favorite.setBackgroundResource(R.drawable.ic_favorite_grey)
                    movie.account_states.favorite = !currentStatus
                    Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                }

            } else
                Toast.makeText(
                    this,
                    it.errorBody()?.charStream().toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()
        }
    }


    private fun setProductionCompanyRv() {
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false).apply {
            rv_production_company.layoutManager = this
        }
        rv_production_company.adapter = ProductionCompanyAdapter(this, movie.production_companies)
    }

    private fun setSimilarMoviesRv() {
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false).apply {
            rv_similar_movies.layoutManager = this
        }
        rv_similar_movies.adapter = MovieListAdapter(context = this, clickListener = this).apply {
            setMoviesList(movie.recommendations.results)
        }

    }

    private fun setCastRv() {
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false).apply {
            rv_cast_layout.layoutManager = this
        }
        rv_cast_layout.adapter = CastAdapter(this, movie.credits.cast)
        var director = "-"
        var writer = "-"

        movie.credits.crew.forEach {
            if (it.job == "Director")
                director = it.name
            if (it.department == "Writing")
                writer = it.name
            if (!(director == "-" || writer == "-"))
                return@forEach
        }
        tv_director.text = director
        tv_writer.text = writer
    }


    private fun setGenreGroup() {
        val generes = movie.genres
        val time = if (generes.size >= 4) 4 else generes.size
        var i = 0

        if (cg_geners.childCount !=0 )
            return
        while (i < time) {
            Chip(this).apply {
                id = generes[i].id
                tag = generes[i].name
                text = generes[i].name

                isCheckable = true
                isCheckedIconVisible = false
                cg_geners.addView(this)

                i++
            }
            cg_geners.invalidate()

            cg_geners.setOnCheckedChangeListener { group: ChipGroup, id: Int ->
                val chip = group.findViewById<Chip>(id)
                startActivity(Intent(this, AllMoviesActivity::class.java).apply {
                    putExtra(GENRE_ID, id)
                    putExtra(AllMoviesActivity.SELECTED_TYPE, AllMoviesActivity.GENRE_MOVIES)
                    putExtra(AllMoviesActivity.SELECTED_TITLE, chip.text)
                })
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadPoster() {
        val url = Constants.IMAGE_BASE_URL + movie.poster_path
        loadImage(url, iv_poster, R.drawable.ic_default_profile)

        tv_rating.text = "${movie.vote_average} / 10"
        tv_overview.text = movie.overview
        tv_duration.text = movie.runtime.toString() + " min"
        tv_year.text = movie.release_date.slice(0..3)
    }


    private fun initializeTrailer() {
        val videos = movie.videos.results
        if (videos.isNotEmpty()) {
            val result = videos.filter {
                it.site == "YouTube" && it.type == "Trailer"
            }
            if (result.isNotEmpty()) {
                this.lifecycle.addObserver(yt_trailer as LifecycleObserver)
                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videos[0].key, 0F)
                    }
                }
                yt_trailer.addYouTubePlayerListener(listener)
                return
            }

        }
        yt_trailer.visibility = View.GONE
        iv_backdrop.visibility = View.VISIBLE
        val backdropUrl = Constants.IMAGE_BASE_URL + movie.backdrop_path
        loadImage(backdropUrl, iv_backdrop, R.drawable.ic_default_profile)

    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(this, MovieDetailActivity::class.java).apply {
            putExtra(MOVIE_ID, movie.id)
        })
    }

    override fun onRateBtnClicked(rating: Double) {
        if (isNetworkAvailable(this))
            movieViewModel.rateTheMovie(movie.id, sessionId!!, rating)
        else
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()


    }

    private fun showNoConnectionSnackBar() {
        snackbar.show()
        comingFromNoInternet = true

    }

    override fun onDestroy() {
        super.onDestroy()
        snackbar.dismiss()
    }

    private fun addNetworkStateObserver() {

        NetworkConnectionLiveData(this).observe(this) {
            if (!it)
                showNoConnectionSnackBar()
            else if (it && comingFromNoInternet) {
                Snackbar.make(
                    this.layout_movie_detail,
                    "Back Online",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setBackgroundTint(resources.getColor(R.color.green))
                }.show()
                movieViewModel.getMovieDetail(movieId, sessionId!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isNetworkAvailable(this))
            showNoConnectionSnackBar()
        else {
            snackbar.dismiss()
            comingFromNoInternet = false
        }
    }

    private fun initializeSnackBar() {
        snackbar = Snackbar.make(
            this.layout_movie_detail,
            "No Connection",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setBackgroundTint(resources.getColor(R.color.snack_bar_bg))
        }
    }

}