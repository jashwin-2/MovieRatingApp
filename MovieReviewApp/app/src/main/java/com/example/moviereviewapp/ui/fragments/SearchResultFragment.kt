package com.example.moviereviewapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereviewapp.R
import com.example.moviereviewapp.model.Movie
import com.example.moviereviewapp.ui.activity.AllMoviesActivity
import com.example.moviereviewapp.ui.activity.MovieDetailActivity
import com.example.moviereviewapp.ui.adapter.AllMovieListAdapter
import com.example.moviereviewapp.ui.adapter.MovieListAdapter
import com.example.moviereviewapp.ui.viewModel.MovieViewModel
import com.example.moviereviewapp.ui.viewModel.SharedViewModel
import com.example.moviereviewapp.utils.MyScrollListener
import com.example.moviereviewapp.utils.NetworkConnectionLiveData
import com.example.moviereviewapp.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search_result.view.*

class SearchResultFragment : Fragment(R.layout.fragment_search_result),
    MovieListAdapter.MovieOnClickListener {

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

        addObserver()
        setRecyclerView(view)
        addNetworkStateObserver()
        return view
    }

    private fun addObserver() {
        var firstPage = movieViewModel.searchMoviesPageCount == 1
        movieViewModel.searchMoviesList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.isVisible = false
                    firstPage = false
                    hideProgressBar()
                    adapter.setMoviesList(it.data?.results?.toList() ?: listOf())
                    val totalPage = it.data!!.total_pages
                    myScrollListener.isLastPage = it.data.page == totalPage

                }
                is Resource.Error -> {
                    if (!firstPage)
                        showProgressBar()
                    else
                        hideProgressBar()
                }
                is Resource.Loading -> {
                    if (firstPage) {
                        shimmerLayout.visibility = View.VISIBLE
                        shimmerLayout.startShimmer()
                    }
                    showProgressBar()
                }
            }
        }

        sharedViewModel.queryText.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setUpScrollListener(it)
                recyclerView.addOnScrollListener(myScrollListener)
                movieViewModel.searchMoviesPageCount = 1
                movieViewModel.searchResponse = MovieViewModel.MovieResponse()
                movieViewModel.searchMovies(it)
            }
        }
    }

    private fun setUpScrollListener(query: String) {
        myScrollListener = MyScrollListener(AllMoviesActivity.SEARCHING) {
            if (it == AllMoviesActivity.SEARCHING)
                movieViewModel.searchMovies(query)
        }
    }

    private fun setRecyclerView(view: View) {
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
            if (it)
                movieViewModel.searchMovies(sharedViewModel.queryText.value!!)
        }

    }
}