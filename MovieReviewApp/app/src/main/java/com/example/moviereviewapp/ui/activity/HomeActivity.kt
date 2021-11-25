package com.example.moviereviewapp.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moviereviewapp.R
import com.example.moviereviewapp.extensions.switch
import com.example.moviereviewapp.model.FavoriteBody
import com.example.moviereviewapp.repository.MovieRepository
import com.example.moviereviewapp.ui.fragments.FavoriteFragment
import com.example.moviereviewapp.ui.fragments.HomeFragment
import com.example.moviereviewapp.ui.fragments.SearchFragment
import com.example.moviereviewapp.ui.fragments.WatchListFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.log


class HomeActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val favoriteFragment = FavoriteFragment()
    private val searchFragment = SearchFragment()
    private val watchListFragment = WatchListFragment()
    private var currentFragment = HOME_FRAGMENT
    private val fragmentManager = supportFragmentManager

    companion object {
        const val HOME_FRAGMENT = "home_fragment"
        const val FAVORITE_FRAGMENT = "favorite_fragment"
        const val WATCHLIST_FRAGMENT = "watchlist_fragment"
        const val SEARCH_FRAGMENT = "search_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setNavigationListener()
        if (savedInstanceState == null)
            fragmentManager.switch(R.id.fragment_container, homeFragment, HOME_FRAGMENT)
    }


    private fun setNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fragmentManager.switch(R.id.fragment_container, homeFragment, HOME_FRAGMENT)
                    currentFragment = HOME_FRAGMENT
                }

                R.id.favorite -> {
                    fragmentManager.switch(
                        R.id.fragment_container,
                        favoriteFragment,
                        FAVORITE_FRAGMENT
                    )
                    currentFragment = FAVORITE_FRAGMENT
                }

                R.id.search -> {
                    fragmentManager.switch(R.id.fragment_container, searchFragment, SEARCH_FRAGMENT)
                    currentFragment = SEARCH_FRAGMENT
                }

                R.id.watch_list -> {
                    fragmentManager.switch(
                        R.id.fragment_container,
                        watchListFragment,
                        WATCHLIST_FRAGMENT
                    )
                    currentFragment = WATCHLIST_FRAGMENT
                }

            }
            true
        }
    }

    override fun onBackPressed() {
        if (currentFragment == HOME_FRAGMENT)
            super.onBackPressed()
        else if (currentFragment == SEARCH_FRAGMENT) {
            val searFragment = fragmentManager.findFragmentByTag(SEARCH_FRAGMENT)

            if (searFragment != null && searFragment.childFragmentManager.backStackEntryCount > 1) {
                searFragment.childFragmentManager.popBackStack()
                return
            }
        }
        goToHomeFragment()
    }

    private fun goToHomeFragment() {
        fragmentManager.switch(R.id.fragment_container, homeFragment, HOME_FRAGMENT)
        currentFragment = HOME_FRAGMENT
        bottomNavigationView.selectedItemId = R.id.home
    }
}