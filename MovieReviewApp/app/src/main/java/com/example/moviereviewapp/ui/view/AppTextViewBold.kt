package com.example.moviereviewapp.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class AppTextViewBold(context: Context, attrs : AttributeSet) : AppCompatTextView(context,attrs){
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeFace = Typeface.createFromAsset(context.assets,"OpenSans-Bold.ttf")
        typeface = typeFace
    }
}