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
import com.example.moviereviewapp.ui.viewModel.SharedViewModel
import com.example.moviereviewapp.utils.MyScrollListener
import com.example.moviereviewapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_search_home.view.*
import kotlinx.android.synthetic.main.fragment_search_result.view.*
import kotlinx.android.synthetic.main.search_toolbar.view.*
import kotlinx.android.synthetic.main.toolbar.*

class SearchFragment : Fragment(R.layout.fragment_search), SearchView.OnQueryTextListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var sharedViewModel: SharedViewModel
    val dashBoardFragment = SearchHomeFragment()
    val resultFragment = SearchResultFragment()


    lateinit var searchLayout: View
    private var queryText: String = " "

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

//        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MovieViewModel::class.java].also {
            movieViewModel = it
        }

        searchLayout = view.toolbar
        searchLayout.sv_search.setOnQueryTextListener(this)

        searchLayout.iv_profile_search.setOnClickListener {
            Intent(activity, ProfileActivity::class.java).apply { startActivity(this) }
        }

        if (savedInstanceState == null)
            childFragmentManager.beginTransaction()
                .replace(R.id.search_fragment_container, dashBoardFragment)
                .addToBackStack(null).commit()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnBackStackChangeListener()
    }

    private fun setOnBackStackChangeListener() {
        childFragmentManager.addOnBackStackChangedListener {
            if (childFragmentManager.backStackEntryCount == 1)
                clearSearchBar()
        }
        setOnTextClearIconClicked()
    }


    override fun onQueryTextSubmit(queryString: String?): Boolean {

        if (!queryString.isNullOrBlank() && queryString != queryText) {
            Log.d("Search", "Called")
            queryText = queryString
            if (childFragmentManager.backStackEntryCount == 1)
                childFragmentManager.beginTransaction()
                    .replace(R.id.search_fragment_container, resultFragment)
                    .addToBackStack(null).commit()

            sharedViewModel.queryText.value = queryString

        }
        return true
    }

    override fun onQueryTextChange(string: String?): Boolean {
        return true
    }

    private fun setOnTextClearIconClicked() {
        val closeButton: View =
            requireView().toolbar.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton.setOnClickListener {
            clearSearchBar()
            childFragmentManager.popBackStack()
        }

    }


    private fun clearSearchBar() {
        requireView().toolbar.sv_search.apply {
            setQuery("", false)
            clearFocus()
        }
    }
}