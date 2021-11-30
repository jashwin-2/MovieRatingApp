package com.example.moviereviewapp.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.moviereviewapp.R
import com.example.moviereviewapp.extensions.switch
import com.example.moviereviewapp.ui.fragments.FavoriteFragment
import com.example.moviereviewapp.ui.fragments.HomeFragment
import com.example.moviereviewapp.ui.fragments.SearchFragment
import com.example.moviereviewapp.ui.fragments.WatchListFragment
import com.example.moviereviewapp.utils.NetworkConnectionLiveData
import com.example.moviereviewapp.utils.isNetworkAvailable
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {
    var comingFromNoInternet = false
    private val homeFragment = HomeFragment()
    private val favoriteFragment = FavoriteFragment()
    private val searchFragment = SearchFragment()
    var snackbar: Snackbar? = null

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

    private fun initializeSnackBar() {
        val bottom = this.bottomNavigationView
        snackbar = Snackbar.make(
            this.fragment_container,
            "No Connection",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setBackgroundTint(resources.getColor(R.color.snack_bar_bg))
            anchorView = bottom
        }
    }

    private fun addNetworkStateObserver() {

        NetworkConnectionLiveData(this).observe(this) {
            if (!it)
                showNoConnectionSnackBar()
            else if (it && comingFromNoInternet) {
                val bottom = this.bottomNavigationView
                Snackbar.make(
                    this.fragment_container,
                    "Back Online",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setBackgroundTint(resources.getColor(R.color.green))
                    anchorView = bottom
                }.show()
            }
        }
    }

    private fun showNoConnectionSnackBar() {
        snackbar?.show()
        comingFromNoInternet = true
        Log.d("Network", "showNoConn")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initializeSnackBar()
        if (!isNetworkAvailable(this)) {
            showNoConnectionSnackBar()
        }

    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(this))
            comingFromNoInternet = false

        else
            showNoConnectionSnackBar()
        addNetworkStateObserver()

    }

    override fun onDestroy() {
        super.onDestroy()
        snackbar?.dismiss()
    }
}