package com.example.tmdbRatingApp.ui.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.ui.adapter.AllReviewAdapter
import com.example.tmdbRatingApp.ui.viewModel.MovieViewModel
import com.example.tmdbRatingApp.utils.Resource
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.tmdbRatingApp.ui.adapter.AllReviewAdapter.Companion.INDEX_OF_SELECTED_REVIEW
import com.example.tmdbRatingApp.ui.fragments.HomeFragment.Companion.MOVIE_ID
import kotlinx.android.synthetic.main.activity_all_revies.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.view.animation.Animation

import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.SimpleItemAnimator


class AllReviewsActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var movieViewModel: MovieViewModel
    lateinit var _adapter: AllReviewAdapter
    var creatingFirstTime = true
    lateinit var toolbar: Toolbar
    var position: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_revies)
        position = intent.getIntExtra(INDEX_OF_SELECTED_REVIEW, 0)
        recyclerView = this.findViewById(R.id.rv_all_reviews)
        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        toolbar = this.toolbar_all_reviews.toolbar_custom
        val movieId = intent.getIntExtra(MOVIE_ID, 0)
        movieViewModel.getMovieReviews(movieId)
        creatingFirstTime = savedInstanceState == null
        setObservers()
        setRecyclerView()

        setBackListener()
        toolbar.title = "All Reviews"
    }

    private fun setBackListener() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@AllReviewsActivity, LinearLayoutManager.VERTICAL, false)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }


    private fun setObservers() {
        movieViewModel.movieReviews.observe(this) {
            if (it is Resource.Success) {
                recyclerView.adapter = AllReviewAdapter(
                    activity = this,
                    type = AllReviewAdapter.ALL_REVIEW_HOLDER,
                    oldReviews = it.data?.results ?: emptyList()
                )
                recyclerView.setHasFixedSize(true)

                if (creatingFirstTime) {
                    recyclerView.scrollToPosition(position)
                    lifecycleScope.launch {
                        delay(200)
                        showBlinkAnimation()
                    }
                }
            }
        }
    }

    private fun showBlinkAnimation() {
        val holder: AllReviewAdapter.ReviewViewHolderAllReviews =
            recyclerView.findViewHolderForAdapterPosition(position) as AllReviewAdapter.ReviewViewHolderAllReviews
        val anim: Animation = AlphaAnimation(0.5f, 1.0f).apply {
            duration = 600
            startOffset = 20
        }

        holder.layout.startAnimation(anim)
    }


}