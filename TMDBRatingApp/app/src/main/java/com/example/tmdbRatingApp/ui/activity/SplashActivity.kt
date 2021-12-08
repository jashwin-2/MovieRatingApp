package com.example.tmdbRatingApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.tmdbRatingApp.R
import com.example.tmdbRatingApp.extensions.hideActionBar
import com.example.tmdbRatingApp.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideActionBar()
        setContentView(R.layout.activity_splash)
        handleSplashScreen()
    }

    companion object {
        var splashLoaded = false
    }

    private fun handleSplashScreen() = if (!splashLoaded) {
        setContentView(R.layout.activity_splash);
        val secondsDelayed = 4
        Handler().postDelayed({
            if (SessionManager(this).fetchAuthToken() == null)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            finish()
        }, (secondsDelayed * 500).toLong())
        splashLoaded = true;
    } else {
        val goToMainActivity: Intent = if (SessionManager(this).fetchAuthToken() != null)
            Intent(this@SplashActivity, HomeActivity::class.java)
        else
            Intent(this@SplashActivity, LoginActivity::class.java)
        goToMainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(goToMainActivity)
        finish()
    }

}