package com.example.tmdbRatingApp.ui.fragments


import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.ui.activity.ProfileActivity
import com.example.tmdbRatingApp.ui.viewModel.MovieViewModel
import com.example.tmdbRatingApp.ui.viewModel.SharedViewModel
import kotlinx.android.synthetic.main.search_toolbar.view.*

class SearchFragment : Fragment(R.layout.fragment_search), SearchView.OnQueryTextListener {
    lateinit var movieViewModel: MovieViewModel
    lateinit var sharedViewModel: SharedViewModel
    private val dashBoardFragment = SearchHomeFragment()
    private val resultFragment = SearchResultFragment()


    lateinit var searchLayout: View
    private var queryText: String = " "

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MovieViewModel::class.java].also {
            movieViewModel = it
        }

        searchLayout = view.toolbar
        searchLayout.sv_search.setOnQueryTextListener(this)

        setClickLinterToProfileIv(view)

        if (savedInstanceState == null)
            childFragmentManager.beginTransaction()
                .replace(R.id.search_fragment_container, dashBoardFragment)
                .addToBackStack(null).commit()

        return view
    }

    private fun setClickLinterToProfileIv(view: View) {
        searchLayout.iv_profile_search.setOnClickListener {
            Intent(activity, ProfileActivity::class.java).apply {
                startActivity(
                    this, ActivityOptions.makeSceneTransitionAnimation(
                        activity, view.iv_profile_search,
                        "iv_profile"
                    ).toBundle()
                )
            }
        }
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

            childFragmentManager.run {
                if (backStackEntryCount > 1)
                    popBackStack()
            }
        }

    }


    private fun clearSearchBar() {
        requireView().toolbar.sv_search.apply {
            setQuery("", false)
            clearFocus()
        }
    }
}