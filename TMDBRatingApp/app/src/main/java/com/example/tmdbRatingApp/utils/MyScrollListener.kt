package com.example.tmdbRatingApp.utils

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyScrollListener(val type:Int , val getMovies : (Int)-> Unit) : RecyclerView.OnScrollListener() {
    var isLoading = false
    var isScrolling = false
    var isLastPage = false
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
            isScrolling = true

    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleItemPosition = layoutManager.childCount
        val totalItem = layoutManager.itemCount
        val isNotLoadingNotLast = !isLoading && !isLastPage
        val isTotalMoreThanVisible = totalItem >= Constants.QUERY_PAGE_SIZE
        val isAtLastItem = firstVisibleItem + firstVisibleItemPosition >= totalItem
        val isNotBeginning = firstVisibleItem >= 0
        val shouldPaginate = isNotLoadingNotLast && isAtLastItem && isNotBeginning &&
                isTotalMoreThanVisible && isScrolling
        if (shouldPaginate) {
          //  recyclerView.setPadding(0, 0, 0, 100)
            getMovies(type)
            isScrolling = false
        }
    }
}

