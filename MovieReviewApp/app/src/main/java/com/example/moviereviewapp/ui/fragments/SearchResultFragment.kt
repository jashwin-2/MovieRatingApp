package com.example.moviereviewapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.model.MovieListResponse
import com.example.moviereviewapp.ui.activity.AllMoviesActivity
import com.example.moviereviewapp.ui.activity.MovieDetailActivity
import com.example.moviereviewapp.ui.adapter.AllMovieListAdapter
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.ui.viewModel.SharedViewModel
import com.example.moviereviewapp.utils.MyScrollListener
import com.example.moviereviewapp.utils.NetworkConnectionLiveData
import com.example.moviereviewapp.utils.Resource
import com.example.moviereviewapp.utils.isNetworkAvailable
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search_result.view.*

class SearchResultFragment : Fragment(R.layout.fragment_search_result),
    MovieListAdapter.MovieOnClickListener {

    private var currentNetworkState = true
    var firstPage = true
    lateinit var movieViewModel: MovieViewModel
    lateinit var adapter: AllMovieListAdapter
    lateinit var paginationProgressBar: ProgressBar
    lateinit var sharedViewModel: SharedViewModel
    lateinit var myScrollListener: MyScrollListener
    lateinit var recyclerView: RecyclerView
    lateinit var shimmerLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_search_result, search_fragment_container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MovieViewModel::class.java].also {
            movieViewModel = it
        }
        if (savedInstanceState != null)
            setUpScrollListener(sharedViewModel.queryText.value ?: "")

        recyclerView = view.rv_result
        paginationProgressBar = view.progress_circular_search
        shimmerLayout = view.findViewById(R.id.shimmer_layout_search)

        currentNetworkState = isNetworkAvailable(activity as Context)
        addObserver()
        setRecyclerView()
        addNetworkStateObserver()
        return view
    }

    private fun addObserver() {

        movieViewModel.searchMoviesList.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    val movies = it.data?.results?.toList() ?: listOf()

                    hideTheLoadingScreen()
                    loadMoviesIfNotEmpty(movies, it, it.data!!)

                }
                is Resource.Error -> {
                    hideProgressBar()
                    val movies = it.data?.results ?: listOf()
                    shimmerLayout.stopShimmer()
                    shimmerLayout.isVisible = false
                    if (firstPage)
                        loadTheCachedMovies(movies)
                    else
                        showLoadOldDataSnackBar(movies)
                }
                is Resource.Loading -> {
                    requireView().no_movies_found.visibility = View.GONE
                    showLoadingScreen()
                }
            }
        }

        sharedViewModel.queryText.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                fetchMoviesListForEnteredQuery(it)

        }
    }

    private fun fetchMoviesListForEnteredQuery(it: String) {
        firstPage = true
        setUpScrollListener(it)
        recyclerView.addOnScrollListener(myScrollListener)
        movieViewModel.searchMoviesPageCount = 1
        movieViewModel.searchResponse = MovieViewModel.MovieResponse()
        movieViewModel.searchMovies(it)
    }

    private fun showLoadingScreen() {
        if (firstPage && currentNetworkState) {
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
            recyclerView.visibility = View.GONE
        } else
            showProgressBar()
    }

    private fun loadTheCachedMovies(movies: List<Movie>) {
        if (movies.isNotEmpty()) {
            recyclerView.scrollToPosition(0)
            recyclerView.isVisible = true
            adapter.setMoviesList(movies)
        } else {
            recyclerView.isVisible = false
            requireView().no_movies_found.isVisible = true
        }
    }

    private fun showLoadOldDataSnackBar(movies: List<Movie>) {
        Snackbar.make(
            requireView().layout_search_fragment,
            "No Connection",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("Load Old Data") {
                hideProgressBar()
                myScrollListener.isLastPage = true
                firstPage = true
                adapter.clearRecyclerView()
                recyclerView.scrollToPosition(0)
                adapter.setMoviesList(movies)
                dismiss()

            }
            anchorView = requireActivity().bottomNavigationView
            setBackgroundTint(resources.getColor(R.color.snack_bar_bg))
        }.show()
    }

    private fun loadMoviesIfNotEmpty(
        movies: List<Movie>,
        it: Resource<MovieListResponse>,
        data: MovieListResponse
    ) {
        if (movies.isNotEmpty()) {
            adapter.setMoviesList(movies)
            val totalPage = it.data!!.total_pages
            myScrollListener.isLastPage = data.page == totalPage
            if (it is Resource.Error)
                recyclerView.scrollToPosition(0)
        } else {
            recyclerView.isVisible = false
            requireView().no_movies_found.isVisible = true
        }
    }

    private fun hideTheLoadingScreen() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        firstPage = false
        hideProgressBar()
    }

    private fun setUpScrollListener(query: String) {
        myScrollListener = MyScrollListener(AllMoviesActivity.SEARCHING) {
            if (it == AllMoviesActivity.SEARCHING)
                movieViewModel.searchMovies(query)
        }
    }

    private fun setRecyclerView() {
        adapter = AllMovieListAdapter(requireContext(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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

    private fun addNetworkStateObserver() {
        NetworkConnectionLiveData(activity as Context).observe(viewLifecycleOwner) {

            if (it && !currentNetworkState) {

                if (myScrollListener.isLastPage) {
                    adapter.clearRecyclerView()
                    recyclerView.visibility = View.GONE
                } else
                    showProgressBar()

                movieViewModel.searchMovies(sharedViewModel.queryText.value!!)

            }
            currentNetworkState = it

        }

    }
}