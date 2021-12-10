package com.example.tmdbRatingApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.extensions.switch
import com.example.tmdbRatingApp.ui.fragments.FavoriteFragment
import com.example.tmdbRatingApp.ui.fragments.HomeFragment
import com.example.tmdbRatingApp.ui.fragments.SearchFragment
import com.example.tmdbRatingApp.ui.fragments.WatchListFragment
import com.example.tmdbRatingApp.utils.NetworkConnectionLiveData
import com.example.tmdbRatingApp.utils.isNetworkAvailable
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {
    var comingFromNoInternet = false
    private val homeFragment = HomeFragment()
    private val favoriteFragment = FavoriteFragment()
    private val searchFragment = SearchFragment()
    var snackbar: Snackbar? = null
    lateinit var networkLiveData: NetworkConnectionLiveData

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

            if (searFragment != null && isSearchFragmentShowingResult(searFragment)) {
                searFragment.childFragmentManager.popBackStack()
                return
            }
        }
        goToHomeFragment()
    }

    private fun isSearchFragmentShowingResult(searFragment: Fragment) =
        searFragment.childFragmentManager.backStackEntryCount > 1

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

        networkLiveData.observe(this) {
            Log.d("Snack", "$it")
            if (!it)
                showNoConnectionSnackBar()
            else if (it && comingFromNoInternet)
                showBackOnlineSnackBar()
        }
    }

    private fun showBackOnlineSnackBar() {
        snackbar?.dismiss()
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


    private fun showNoConnectionSnackBar() {
        if (snackbar == null)
            initializeSnackBar()
        snackbar!!.show()
        comingFromNoInternet = true
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
        networkLiveData = NetworkConnectionLiveData(this)
        if (isNetworkAvailable(this))
            comingFromNoInternet = false
        else
            showNoConnectionSnackBar()
        addNetworkStateObserver()
    }

    override fun onPause() {
        super.onPause()
        networkLiveData.removeObservers(this)
    }


    override fun onStop() {
        super.onStop()
        snackbar?.dismiss()
    }
}