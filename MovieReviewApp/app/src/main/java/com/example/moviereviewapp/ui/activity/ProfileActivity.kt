package com.example.moviereviewapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.moviereviewapp.R
import com.example.moviereviewapp.utils.SessionManager
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.user_detail_layout.*

class ProfileActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setUserDetails()
        toolbar = findViewById<View>(R.id.profile_toolbar).toolbar_custom

        toolbar.title = "Profile"
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun setUserDetails() {
        val sessionManager = SessionManager(this).apply {
            tv_user_name_profile.text = fetchUserName()
            tv_acc_id_profile.text = fetchAccId().toString()
            tv_include_adut_profile.text = fetchIncludeAdult().toString()
        }

        btn_log_out.setOnClickListener {

            sessionManager.apply {
                saveAuthToken(null)
                Intent(this@ProfileActivity, LoginActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(this)
                    finish()
                }
            }
        }
    }
}