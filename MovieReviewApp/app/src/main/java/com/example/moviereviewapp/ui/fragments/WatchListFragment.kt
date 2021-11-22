package com.example.moviereviewapp.ui.fragments

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
import com.example.moviereviewapp.ui.adapter.AllMovieListAdapter
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.Resource
import com.example.moviereviewapp.utils.SessionManager
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_watchlist.*
import kotlinx.android.synthetic.main.fragment_watchlist.view.*

class WatchListFragment : Fragment(R.layout.fragment_favorite),
    MovieListAdapter.MovieOnClickListener {

    private lateinit var shimmerFrameLayout : ShimmerFrameLayout
    lateinit var watchListRv : RecyclerView
    lateinit var movieViewModel: MovieViewModel
    lateinit var favAdapter: AllMovieListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        val sessionManager = SessionManager(activity as Context)
        val id = sessionManager.fetchAccId()
        val sessionId = sessionManager.fetchAuthToken()

        watchListRv = view.rv_watch_list
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout_watchlist)

        movieViewModel.getWatchListMovies(id, sessionId!!)
        favAdapter = AllMovieListAdapter(requireContext(), this)
        addObservers()
        return view
    }

    private fun addObservers() {
        movieViewModel.watchListMovies.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    setUpRecyclerView()
                    favAdapter.setMoviesList(it.data?.results ?: listOf())
                }
                is Resource.Loading -> shimmerFrameLayout.startShimmer()
                is Resource.Error -> Toast.makeText(
                    requireContext(),
                    it.error.status_message,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun setUpRecyclerView() {
        watchListRv.visibility = View.VISIBLE
        watchListRv.adapter = favAdapter
        watchListRv.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(activity, MovieDetailActivity::class.java).apply {
            putExtra(HomeFragment.MOVIE_ID, movie.id)
        })
    }
}