package com.example.moviereviewapp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.moviereviewapp.extensions.hideActionBar
import com.example.moviereviewapp.ui.viewModel.LoginViewModel
import com.example.moviereviewapp.utils.Constants
import com.example.moviereviewapp.utils.Resource
import com.example.moviereviewapp.utils.SessionManager
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {
    lateinit var viewModel: LoginViewModel
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideActionBar()
        setContentView(com.example.moviereviewapp.R.layout.login_activity)
        setUpErrorMode()

        sessionManager = SessionManager(this)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        addObservers()
        btn_login.setOnClickListener {
            login()
        }

        setOnClickListener()
    }

    private fun setOnClickListener() {
        tv_forgot_password.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FORGOT_PASSWORD_URL))
            startActivity(browserIntent)
        }

        tv_register.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.SIGNUP_URL))
            startActivity(browserIntent)
        }

    }

    private fun addObservers() {

        viewModel.loginResponse.observe(this) {
            progress_circular.visibility = View.GONE
            when (it) {
                is Resource.Loading -> progress_circular.visibility = View.VISIBLE

                is Resource.Error -> {
                    Toast.makeText(this, it.error.status_message, Toast.LENGTH_LONG).show()
                    if (it.error.status_code == Constants.INVALID_LOGIN_CREDENTIALS) {
                        til_username.error = "Invalid username or password"
                        til_password.error = "Invalid username or password"
                    }
                }
                is Resource.Success -> {
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_LONG).show()
                    viewModel.sessionId.value?.data?.let { it1 ->
                        val account = viewModel.account.value?.data

                        sessionManager.apply {
                            saveAuthToken(it1.session_id)
                            saveAccId(account?.id ?: 0)
                            saveUserName(account?.username ?: "")
                            saveIncludeAdult(account?.include_adult ?: false)
                        }

                        Log.d("ID", "${sessionManager.fetchAccId()}")
                    }
                    finish()
                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }
        }
    }

    private fun login() {
        progress_circular.visibility = View.VISIBLE
        viewModel.login(et_username.text.toString(), et_password.text.toString())

    }

    private fun setUpErrorMode() {
        et_username.doOnTextChanged { text, _, _, _ ->
            if (text!!.isEmpty())
                til_username.error = "Can't be empty"
            else if (text.isNotEmpty())
                til_username.error = null
        }
        et_password.doOnTextChanged { text, start, before, count ->
            if (text!!.isEmpty())
                til_password.error = "Can't be empty"
            else if (text.isNotEmpty())
                til_password.error = null
        }
    }

}