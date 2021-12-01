package com.example.moviereviewapp.ui.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.ui.activity.MovieDetailActivity
import com.example.moviereviewapp.ui.activity.ProfileActivity
import com.example.moviereviewapp.ui.adapter.AllMovieListAdapter
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.NetworkConnectionLiveData
import com.example.moviereviewapp.utils.Resource
import com.example.moviereviewapp.utils.SessionManager
import com.example.moviereviewapp.utils.isNetworkAvailable
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_all_movies.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_watchlist.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.ConnectException

class WatchListFragment : Fragment(R.layout.fragment_favorite),
    MovieListAdapter.MovieOnClickListener {

    private lateinit var shimmerFrameLayout: ShimmerFrameLayout


    var comingFromNoInternet = false
    lateinit var watchListRv: RecyclerView
    lateinit var movieViewModel: MovieViewModel
    lateinit var watchAdapter: AllMovieListAdapter
    lateinit var sessionId: String
    var accountId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)
        // movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MovieViewModel::class.java].also {
            movieViewModel = it
        }
        SessionManager(activity as Context).apply {
            accountId = fetchAccId()
            sessionId = fetchAuthToken()!!
        }
        movieViewModel.getWatchListMovies(accountId,sessionId)

        watchListRv = view.rv_watch_list
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout_watchlist)

        watchAdapter = AllMovieListAdapter(requireContext(), this)
        addObservers()

        view.iv_profile_watchlist.setOnClickListener {
            Intent(activity, ProfileActivity::class.java).apply {
                startActivity(
                    this, ActivityOptions.makeSceneTransitionAnimation(
                        activity, view.iv_profile_watchlist,
                        "iv_profile"
                    ).toBundle()
                )
            }        }

        setUpRecyclerView()
        addNetworkStateObserver()

        return view
    }

    private fun addObservers() {
        movieViewModel.watchListMovies.observe(viewLifecycleOwner) {
            val newList = it.data?.results ?: listOf()
            when (it) {
                is Resource.Success -> {

                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    watchListRv.visibility = View.VISIBLE
                    adjustScrollPositionIfChanged(newList)

                    watchAdapter.setMoviesList(newList)
                }

                is Resource.Loading -> shimmerFrameLayout.startShimmer()

                is Resource.Error -> {
                    if (!newList.isNullOrEmpty()) {
                        shimmerFrameLayout.stopShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        watchListRv.visibility = View.VISIBLE
                        adjustScrollPositionIfChanged(newList)

                        watchAdapter.setMoviesList(newList)
                    }

                }
            }

        }
    }

    private fun adjustScrollPositionIfChanged(newList: List<Movie>) {

        if (!(newList.size <= watchAdapter.itemCount && watchAdapter.oldMovies.containsAll(
                newList
            ))
        )
            watchListRv.layoutManager?.scrollToPosition(0)
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)
            movieViewModel.getWatchListMovies(accountId, sessionId)
    }

    private fun setUpRecyclerView() {
        watchListRv.adapter = watchAdapter
        watchListRv.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(activity, MovieDetailActivity::class.java).apply {
            putExtra(HomeFragment.MOVIE_ID, movie.id)
        })
    }

    private fun addNetworkStateObserver() {
        NetworkConnectionLiveData(activity as Context).observe(viewLifecycleOwner) {
            if (it)
                movieViewModel.getWatchListMovies(accountId, sessionId)
        }

    }
}