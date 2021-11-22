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

class FavoriteFragment : Fragment(R.layout.fragment_favorite),
    MovieListAdapter.MovieOnClickListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var favAdapter: AllMovieListAdapter
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var favRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        val sessionManager = SessionManager(activity as Context)
        val id = sessionManager.fetchAccId()
        val sessionId = sessionManager.fetchAuthToken()

        favRecyclerView = view.findViewById(R.id.rv_favorite)
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout)

        movieViewModel.getFavoriteMovies(id, sessionId!!)
        favAdapter = AllMovieListAdapter(requireContext(), this)
        addObservers()
        return view
    }

    private fun addObservers() {
        movieViewModel.favoriteMovies.observe(viewLifecycleOwner) {
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
        favRecyclerView.visibility = View.VISIBLE
        favRecyclerView.adapter = favAdapter
        favRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onClick(movie: Movie) {
        startActivity(Intent(activity, MovieDetailActivity::class.java).apply {
            putExtra(HomeFragment.MOVIE_ID, movie.id)
        })
    }
}