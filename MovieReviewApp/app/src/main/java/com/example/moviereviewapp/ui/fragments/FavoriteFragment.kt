package com.example.moviereviewapp.ui.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.ui.activity.MovieDetailActivity
import com.example.moviereviewapp.ui.activity.ProfileActivity
import com.example.moviereviewapp.ui.adapter.AllMovieListAdapter
import com.example.moviereviewapp.ui.adapter.MovieListOnClickListener
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.utils.NetworkConnectionLiveData
import com.example.moviereviewapp.utils.Resource
import com.example.moviereviewapp.utils.SessionManager
import com.example.moviereviewapp.utils.startMovieDetailActivity
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.fragment_favorite.view.*

class FavoriteFragment : Fragment(R.layout.fragment_favorite),
    MovieListOnClickListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var favAdapter: AllMovieListAdapter
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var favRecyclerView: RecyclerView

    private var accountId: Int = 0
    lateinit var sessionId: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

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
        favRecyclerView = view.findViewById(R.id.rv_favorite)
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout)

        favAdapter = AllMovieListAdapter(requireContext(), this)
        addObservers()

        setClickListenerToProfileIv(view)
        setUpRecyclerView()
        addNetworkStateObserver()
        return view
    }

    private fun setClickListenerToProfileIv(view: View) {
        view.iv_profile_favorite.setOnClickListener {
            Intent(activity, ProfileActivity::class.java).apply {
                startActivity(
                    this, ActivityOptions.makeSceneTransitionAnimation(
                        activity, view.iv_profile_favorite,
                        "iv_profile"
                    ).toBundle()
                )
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)
            movieViewModel.getFavoriteMovies(accountId, sessionId)
    }


    private fun addObservers() {
        movieViewModel.favoriteMovies.observe(viewLifecycleOwner) {
            val newList = it.data?.results ?: listOf()

            when (it) {
                is Resource.Success -> {
                    loadMoviesToRv(newList)
                }
                is Resource.Loading -> shimmerFrameLayout.startShimmer()
                is Resource.Error -> {
                    if (!newList.isNullOrEmpty())
                        loadMoviesToRv(newList)
                }
            }

        }
    }

    private fun loadMoviesToRv(newList: List<Movie>) {
        shimmerFrameLayout.stopShimmer()
        favRecyclerView.visibility = View.VISIBLE
        shimmerFrameLayout.visibility = View.GONE
        adjustScrollPositionIfChanged(newList)
        favAdapter.setMoviesList(newList)
    }

    private fun adjustScrollPositionIfChanged(newList: List<Movie>) {

        if (isBothListsAreDifferent(newList))
            favRecyclerView.layoutManager?.scrollToPosition(0)    }

    private fun isBothListsAreDifferent(newList: List<Movie>): Boolean {
        return !(newList.size <= favAdapter.itemCount && favAdapter.oldMovies.containsAll(
            newList
        ))
    }

    private fun setUpRecyclerView() {
        favRecyclerView.adapter = favAdapter
        favRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }


    private fun addNetworkStateObserver() {
        NetworkConnectionLiveData(activity as Context).observe(viewLifecycleOwner) {
            if (it)
                movieViewModel.getFavoriteMovies(accountId, sessionId)
        }

    }

    override fun onClick(movie: Movie, holder: AllMovieListAdapter.ViewHolder) {
        startMovieDetailActivity(requireActivity() , movie ,holder.poster)
    }
}