package com.example.tmdbRatingApp.ui.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.model.Review
import com.example.tmdbRatingApp.ui.activity.AllReviewsActivity
import com.example.tmdbRatingApp.ui.fragments.HomeFragment.Companion.MOVIE_ID
import com.example.tmdbRatingApp.ui.view.AppTextView
import com.example.tmdbRatingApp.ui.view.AppTextViewBold
import com.example.tmdbRatingApp.utils.Constants


class AllReviewAdapter(
    val activity: Context,
    val type: Int,
    val movieId: Int = 0,
    var oldReviews: List<Review> = listOf()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MOVIE_DETAIL_HOLDER = 1
        const val ALL_REVIEW_HOLDER = 2
        const val INDEX_OF_SELECTED_REVIEW = "position"
    }


    inner class ReviewViewHolderMovieDetail(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.layout_reviews_holder)
        val content: AppTextView = view.findViewById(R.id.expandable_text)
        val rating: AppTextViewBold = view.findViewById(R.id.tv_rating_reviews)
        val tittle: AppTextViewBold = view.findViewById(R.id.tv_all_review_tittle)
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)

        fun bind(review: Review, position: Int) {
            Glide.with(activity)
                .load(Constants.IMAGE_BASE_URL + review.author_details.avatar_path)
                .fitCenter()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_24)
                .into(avatar)
            tittle.text = "A review by ${review.author}"
            tittle.isSelected = true
            rating.text = review.author_details.rating.toString()
            content.text = review.content

            layout.setOnClickListener {
                Intent(activity, AllReviewsActivity::class.java).apply {
                    putExtra(INDEX_OF_SELECTED_REVIEW, position)
                    putExtra(MOVIE_ID, movieId)
                    startActivity(activity, this, null)
                }

            }
        }
    }

    inner class ReviewViewHolderAllReviews(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.layout_all_reviews_holder)
        val content: AppTextView = view.findViewById(R.id.tv_content)
        val rating: AppTextViewBold = view.findViewById(R.id.tv_rating_reviews)
        private val tittle: AppTextViewBold = view.findViewById(R.id.tv_all_review_tittle)
        private val avatar: ImageView = view.findViewById(R.id.iv_avatar)
        var collapsedHeight: Int = 0
        val expandArrow: ImageView = view.findViewById(R.id.iv_expand_arrow)

        fun bind(review: Review, position: Int) {
            content.apply {
                text = review.content
                maxLines = if (review.isExpanded) Int.MAX_VALUE else 8
            }

            if (review.isExpanded)
                expandArrow.setImageResource(R.drawable.ic_up_arrow)
            else
                expandArrow.setImageResource(R.drawable.ic_down_arrow)
            tittle.isSelected = true


            content.post {
                if (content.lineCount > 8) {
                    expandArrow.visibility = View.VISIBLE
                    layout.setOnClickListener {
                        val anim = AlphaAnimation(0.5f, 1.0f).apply {
                            duration = 1000
                            startOffset = 40
                        }
                        it.startAnimation(anim)

                  //     expandCollapsedByMaxLines(content,this,review.isExpanded)
                        notifyItemChanged(position)

                        review.isExpanded = !review.isExpanded
                    }
                } else
                    expandArrow.visibility = View.GONE
            }


            Glide.with(activity)
                .load(Constants.IMAGE_BASE_URL + review.author_details.avatar_path)
                .fitCenter()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person_24)
                .into(avatar)
            tittle.text = "A review by ${review.author}"
            rating.text = review.author_details.rating.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (type == ALL_REVIEW_HOLDER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.all_reviews_holder, parent, false)
            ReviewViewHolderAllReviews(view)

        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.reviews_holder, parent, false)
            ReviewViewHolderMovieDetail(view)
        }
    }

    fun setReviews(newList: List<Review>) {
        oldReviews = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = oldReviews.size


    @SuppressLint("Range")
    fun expandCollapsedByMaxLines(
        text: AppTextView,
        holder: ReviewViewHolderAllReviews,
        isExpanded: Boolean
    ) {
        val height = text.measuredHeight
        var newHeight: Int = 0

        if (holder.collapsedHeight == 0)
            holder.collapsedHeight = height
        text.height = height

        if (isExpanded) {
            text.maxLines = 8
            newHeight = holder.collapsedHeight
            holder.expandArrow.setImageResource(R.drawable.ic_down_arrow)
       } else {
           text.maxLines = Int.MAX_VALUE
           text.measure(
               View.MeasureSpec.makeMeasureSpec(text.measuredWidth, View.MeasureSpec.EXACTLY),
               View.MeasureSpec.makeMeasureSpec(
                   ViewGroup.LayoutParams.WRAP_CONTENT,
                   View.MeasureSpec.UNSPECIFIED
               )
           )
           newHeight = text.measuredHeight
           holder.expandArrow.setImageResource(R.drawable.ic_up_arrow)
    }
     //   holder.isExpanded = true
        val animation = ObjectAnimator.ofInt(text, "height", height, newHeight)
        animation.setDuration(10).start()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = oldReviews[position]
        if (holder is AllReviewAdapter.ReviewViewHolderAllReviews)
            holder.bind(review, position)
        else if (holder is AllReviewAdapter.ReviewViewHolderMovieDetail)
            holder.bind(review, position)
    }

}