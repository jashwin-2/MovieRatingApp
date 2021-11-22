package com.example.moviereviewapp.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.moviereviewapp.R
import com.ramotion.fluidslider.FluidSlider
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.dialoge_rating.*
import kotlinx.android.synthetic.main.dialoge_rating.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class RatingFragment : DialogFragment() {
    private lateinit var slider: FluidSlider
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
        // dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        slider = view.rating_listener

        view.iv_close.setOnClickListener {
            dismiss()
        }
        setRatingListener()

        view.btn_rate.setOnClickListener {
            dismiss()
            Toast.makeText(activity, "Movie Rated Successfully", Toast.LENGTH_LONG).show()
        }
    }

    private fun setRatingListener() {
        val max = 10
        val min = 0.5
        val total = 9.5

        slider.positionListener = { pos ->
            slider.bubbleText = "${roundOffDecimal(total * pos + min)}"
            tv_selected_rating.text = slider.bubbleText
        }
        slider.position = 0.25f
        slider.startText = "$min"
        slider.endText = "$max"

    }

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }
}