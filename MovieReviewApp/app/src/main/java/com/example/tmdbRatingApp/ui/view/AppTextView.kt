package com.example.tmdbRatingApp.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class AppTextView(context: Context, attrs : AttributeSet) : AppCompatTextView(context,attrs){
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeFace = Typeface.createFromAsset(context.assets,"OpenSans-Regular.ttf")
        typeface = typeFace
    }
}