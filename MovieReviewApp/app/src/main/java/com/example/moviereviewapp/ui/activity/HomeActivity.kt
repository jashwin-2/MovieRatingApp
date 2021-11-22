package com.example.moviereviewapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moviereviewapp.R
import com.example.moviereviewapp.extensions.switch
import com.example.moviereviewapp.ui.fragments.FavoriteFragment
import com.example.moviereviewapp.ui.fragments.HomeFragment
import com.example.moviereviewapp.ui.fragments.SearchFragment
import com.example.moviereviewapp.ui.fragments.WatchListFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val favoriteFragment = FavoriteFragment()
    private val searchFragment = SearchFragment()
    private val watchListFragment = WatchListFragment()
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
                R.id.home ->
                    fragmentManager.switch(R.id.fragment_container, homeFragment, HOME_FRAGMENT)

                R.id.favorite ->
                    fragmentManager.switch(
                        R.id.fragment_container,
                        favoriteFragment,
                        FAVORITE_FRAGMENT
                    )

                R.id.search ->
                    fragmentManager.switch(R.id.fragment_container, searchFragment, SEARCH_FRAGMENT)

                R.id.watch_list ->
                    fragmentManager.switch(
                        R.id.fragment_container,
                        watchListFragment,
                        WATCHLIST_FRAGMENT
                    )
            }
            true
        }
    }

}