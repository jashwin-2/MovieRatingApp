package com.example.tmdbRatingApp.extensions

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.imageloading.CustomImageLoader
import com.example.tmdbRatingApp.R

fun AppCompatActivity.hideActionBar() {
    requestWindowFeature(Window.FEATURE_NO_TITLE)

    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )

    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}

fun AppCompatActivity.loadImage(url: String, view: ImageView, defaultImage: Int = R.drawable.ic_default_movie) {

    val imageLoader: CustomImageLoader =  CustomImageLoader.getInstance(this)
    imageLoader.displayImage(url,view,defaultImage)

//    Glide.with(this)
//        .load(url)
//        .submit()
}

fun FragmentManager.switch(containerId: Int, newFrag: Fragment, tag: String) {

    var current = findFragmentByTag(tag)
    beginTransaction()
        .apply {

            primaryNavigationFragment?.let { hide(it) }

            if (current == null) {
                current = newFrag
                add(containerId, current!!, tag)
            } else {
                show(current!!)
            }
        }
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .setPrimaryNavigationFragment(current)
        .setReorderingAllowed(true)
        .commitNowAllowingStateLoss()
}