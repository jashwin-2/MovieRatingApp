package com.example.tmdbRatingApp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.tmdbRatingApp.R
import com.ramotion.fluidslider.FluidSlider
import kotlinx.android.synthetic.main.dialoge_rating.*
import kotlinx.android.synthetic.main.dialoge_rating.view.*
import java.util.*
import kotlin.math.roundToInt

class RatingFragment(private val ratingListener: RatingListener) : DialogFragment() {
    private lateinit var slider: FluidSlider
    var rating: Double = 0.0

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Objects.requireNonNull(dialog)?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialoge_rating, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slider = view.rating_listener

        setClickListenersToBtns(view)
        setRatingListener()

    }

    private fun setClickListenersToBtns(view: View) {
        view.iv_close.setOnClickListener {
            dismiss()
        }
        view.btn_rate.setOnClickListener {
            dismiss()
            ratingListener.onRateBtnClicked(rating)
        }
    }

    private fun setRatingListener() {
        val max = 10
        val min = 0.5
        val total = 9.5

        slider.positionListener = { pos ->
            rating = roundToHalf(total * pos + min)
            slider.bubbleText = "$rating"
            tv_selected_rating.text = slider.bubbleText
        }
        slider.position = 0.25f
        slider.startText = "$min"
        slider.endText = "$max"

    }


    private fun roundToHalf(d: Double): Double {
        return (d * 2).roundToInt() / 2.0
    }
}

interface RatingListener {
    fun onRateBtnClicked(rating: Double)
}